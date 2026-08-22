package cn.ykcryobs.ptportal.ptp

import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.util.Log
import cn.ykcryobs.ptportal.AppContextHolder
import cn.ykcryobs.ptportal.ptp.constants.PtpConstants
import cn.ykcryobs.ptportal.ptp.constants.PtpContainerType
import cn.ykcryobs.ptportal.ptp.constants.PtpResponseCode
import cn.ykcryobs.ptportal.ptp.constants.PtpStandardOpCode
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.log

class UsbPtpSession {
    @Volatile
    private var ptpInterface: UsbInterface? = null

    @Volatile
    var connection: UsbDeviceConnection? = null
        private set

    @Volatile
    var bulkOutEndpoint: UsbEndpoint? = null
        private set

    @Volatile
    var bulkInEndpoint: UsbEndpoint? = null
        private set

    private val transactionId = AtomicInteger(1)

    @Synchronized
    fun openDevice(device: UsbDevice): Boolean {
        close();

        val usbManager = AppContextHolder.usbManager
        val conn = usbManager.openDevice(device) ?: run {
            Log.e("PTPortal_PTP", "openDevice 返回null，没有权限或者设备被占用")
            return false
        }
        connection = conn

        for (interfaceIndex in 0 until device.interfaceCount) {
            val iface = device.getInterface(interfaceIndex)
            if (iface.interfaceClass == 6 && iface.interfaceSubclass == 1) {
                ptpInterface = iface
                if (!conn.claimInterface(iface, true)) {
                    Log.e("PTPortal_PTP", "claimInterface失败")
                    close()
                    return false
                }

                for (epIdx in 0 until iface.endpointCount) {
                    val ep = iface.getEndpoint(epIdx)
                    if (ep.type == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        if (ep.direction == UsbConstants.USB_DIR_OUT) {
                            bulkOutEndpoint = ep
                        } else {
                            bulkInEndpoint = ep
                        }
                    }
                }
            }
        }

        if (bulkInEndpoint == null || bulkOutEndpoint == null) {
            Log.e("PTPortal_PTP", "找不到PTP Bulk端点")
            close()
            return false
        }
        Log.i("PTPortal_PTP", "PTP端点查找成功")
        return true
    }

    @Synchronized
    fun close() {
        val connSnap = connection
        val ifaceSnap = ptpInterface

        if (connSnap != null && ifaceSnap != null) {
            try {
                connSnap.releaseInterface(ifaceSnap)
                Log.i("PTPortal_PTP", "close: releaseInterface ok")
            } catch (e: Exception) {
                Log.w("PTPortal_PTP", "close: releaseInterface exception", e)
            }
        }

        connSnap?.close()
        Log.i("PTPortal_PTP", "close: connection closed")

        transactionId.set(1)

        connection = null
        bulkInEndpoint = null
        bulkOutEndpoint = null
        ptpInterface = null
    }

    //-------------------------------------------

    fun sendCommand(opCode: PtpStandardOpCode, vararg params: Int): PtpResponse {

        val tId = sendCommandOnly(opCode, *params)
        if (tId == -1) {
            return PtpResponse(PtpResponseCode.GENERAL_ERROR, -1)
        }

        return readResponse(tId)

    }

    fun sendCommandWithDataIn(opCode: PtpStandardOpCode, vararg params: Int): PtpDataResponse {
        val tId = sendCommandOnly(opCode, *params)
        return readDataAndResponse(tId)
    }

    private fun sendCommandOnly(
        opCode: PtpStandardOpCode, vararg params: Int
    ): Int {
        val tId = transactionId.getAndIncrement()
        val paramCount = params.size
        val totalLen = PtpConstants.HEADER_SIZE + paramCount * 4

        // 构造 12 字节包头
        val buffer = ByteBuffer.allocate(totalLen).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putInt(totalLen)                         // 总长
        buffer.putShort(PtpContainerType.COMMAND.getShort()) // 容器类型=命令
        buffer.putShort(opCode.getShort())         // 操作码
        buffer.putInt(tId)                             // 事务ID
        params.forEach { buffer.putInt(it) }           // 参数

        val sendLen = connection?.bulkTransfer(
            bulkOutEndpoint, buffer.array(), totalLen, PtpConstants.USB_TIMEOUT
        )
        if (sendLen != totalLen) {
            Log.e("PTPortal_PTP", "命令发送失败: 期望$totalLen 实际$sendLen")
            return -1
        }

        val data = buffer.array()
        Log.d(
            PtpConstants.LOG_TAG,
            "发送命令: opCode=0x${opCode.code.toString(16).padStart(4, '0')}, tId=$tId"
        )
        Log.d(PtpConstants.LOG_TAG, "命令包字节: ${data.joinToString(" ") { "%02x".format(it) }}")
        Log.d(PtpConstants.LOG_TAG, "bulkTransfer(OUT) 返回: $sendLen (期望 $totalLen)")

        return tId
    }

    private fun readResponse(
        expectedTId: Int, timeoutMs: Int = PtpConstants.USB_TIMEOUT
    ): PtpResponse {
        val buffer = ByteArray(PtpConstants.HEADER_SIZE + PtpConstants.RESPONSE_MAX_PARAMS)
        val ret = connection!!.bulkTransfer(bulkInEndpoint, buffer, buffer.size, timeoutMs)

        if (ret < PtpConstants.HEADER_SIZE) {
            Log.e(PtpConstants.LOG_TAG, "读取响应包失败: 期望 >=12 实际$ret")
            return PtpResponse(PtpResponseCode.GENERAL_ERROR, expectedTId)
        }

        val respBuf = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN)
        val length = respBuf.getInt()
        val type = respBuf.getShort().toInt() and 0xFFFF
        val code = respBuf.getShort().toInt() and 0xFFFF
        val tId = respBuf.getInt()

        if (type != PtpContainerType.RESPONSE.code) {
            Log.w(PtpConstants.LOG_TAG, "意外的包头，期望 response，实际：$type")

            if (type == PtpContainerType.DATA.code) {
                // 消耗可能无用的 Data 包后重试
                val remaining = length - ret
                if (remaining > 0) {
                    val buf = ByteArray(PtpConstants.USB_TRANSFER_BUFFER)
                    var left = remaining
                    while (left > 0) {
                        val read = connection!!.bulkTransfer(
                            bulkInEndpoint, buf, buf.size, PtpConstants.USB_TIMEOUT
                        )
                        if (read <= 0) break
                        left -= read
                    }
                }


                return readResponse(tId)
            }
        }

        val paramCount = (ret - PtpConstants.HEADER_SIZE) / 4
        val params = IntArray(paramCount) { respBuf.getInt() }

        return PtpResponse(code, tId, params)
    }

    private fun readDataAndResponse(tId: Int): PtpDataResponse {
        val headerBuf = ByteArray(PtpConstants.USB_TRANSFER_BUFFER)
        val ret = connection?.bulkTransfer(
            bulkInEndpoint, headerBuf, headerBuf.size, PtpConstants.USB_TIMEOUT
        )

        if (ret == null || ret < PtpConstants.HEADER_SIZE) {
            Log.e(PtpConstants.LOG_TAG, "读取响应包失败: 期望 >=12 实际$ret")
            return PtpDataResponse(PtpResponseCode.GENERAL_ERROR, tId, ByteArray(0))
        }

        val buffer = ByteBuffer.wrap(headerBuf, 0, ret).order(ByteOrder.LITTLE_ENDIAN)
        val totalLength = buffer.getInt()
        val type = buffer.getShort().toInt() and 0xFFFF
        val code = buffer.getShort().toInt() and 0xFFFF
        val tId = buffer.getInt()
        if (type == PtpContainerType.RESPONSE.code) {
            return PtpDataResponse(code, tId, ByteArray(0))
        }
        if (type != PtpContainerType.DATA.code) {
            Log.w(PtpConstants.LOG_TAG, "意外的包头，期望 data，实际：$type")
            return PtpDataResponse(PtpResponseCode.GENERAL_ERROR, tId, ByteArray(0))
        }

        val dataSize = totalLength - PtpConstants.HEADER_SIZE
        val output = ByteArrayOutputStream(dataSize.coerceAtMost(PtpConstants.USB_TRANSFER_BUFFER))

        val firstChunkSize = ret - PtpConstants.HEADER_SIZE
        if (firstChunkSize > 0) {
            output.write(headerBuf, PtpConstants.HEADER_SIZE, firstChunkSize)
        }

        var totalRead = firstChunkSize
        while (totalRead < dataSize) {
            val chunkRead = connection!!.bulkTransfer(
                bulkInEndpoint, headerBuf, headerBuf.size, PtpConstants.USB_TIMEOUT
            )
            if (chunkRead <= 0) break
            output.write(headerBuf, 0, chunkRead)
            totalRead += chunkRead
        }

        val data = output.toByteArray()

        val response = readResponse(tId)

        return PtpDataResponse(response.respCode, tId, data)

    }

    // ---------------------------------------


    fun openSession(): Boolean {
        val (resp) = sendCommand(PtpStandardOpCode.OPEN_SESSION, 0x01)
        return if (resp == PtpResponseCode.OK) {
            Log.i("PTPortal_PTP", "PTP 会话打开成功")
            true
        } else {
            Log.e("PTPortal_PTP", "PTP 会话打开失败，响应=$resp")
            false
        }
    }

    fun sdioConnect(prase: Int): Boolean {
        val (resp) = sendCommandWithDataIn(PtpStandardOpCode.SDIO_CONNECT, prase, 0x00, 0x00)
        return if (resp == PtpResponseCode.OK) {
            Log.i("PTPortal_PTP", "SDIO 连接成功，prase=$prase")
            true
        } else {
            Log.e("PTPortal_PTP", "SDIO 连接失败，prase=$prase，响应=$resp")
            false
        }

    }

    fun sdioGetExtDeviceInfo(version: Int): Boolean {
        val (resp, tId, data) = sendCommandWithDataIn(
            PtpStandardOpCode.SDIO_GET_EXT_DEVICE_INFO, version
        )
        if (resp != PtpResponseCode.OK) {
            Log.e("PTPortal_PTP", "GetDeviceInfo 失败")
            return false
        }

        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        if (buffer.remaining() < 2) return false
        val sdioVer = buffer.short.toInt() and 0xFFFF
        Log.d("PTPortal_PTP", "SDIExtensionVersion = 0x%04X".format(sdioVer))

        // NOTE 暂时直解析第一个参数，底二三个参数组丢弃

        // NOTE 要求 版本为 0x12c（3.0.0）
        return sdioVer == 0x12c

    }

    fun getDeviceInfo(): String? {
        val (resp, tId, data) = sendCommandWithDataIn(PtpStandardOpCode.GET_DEVICE_INFO)
        if (resp != PtpResponseCode.OK) {
            Log.e("PTPortal_PTP", "GetDeviceInfo 失败")
            return null
        }
        val readable = String(data, Charsets.UTF_8).filter { it.code in 32..126 }.take(128)
        Log.i("PTPortal_PTP", "设备信息: $readable")
        return readable
    }

    fun resetDevice() {
        if (connection == null || bulkOutEndpoint == null || bulkOutEndpoint == null) {
            return
        }
        // Step 1: Cancel any pending PTP request (0x64)
        val cancelResult = connection!!.controlTransfer(
            0x21, 0x64, 0, 0, null, 0, 2000
        )
        Log.d(PtpConstants.LOG_TAG, "PTP cancel request result: $cancelResult")
        Thread.sleep(100)

        // Step 2: PTP Device Reset (0x66)
        val resetResult = connection!!.controlTransfer(
            0x21, 0x66, 0, 0, null, 0, 5000
        )
        Log.d(PtpConstants.LOG_TAG, "PTP device reset result: $resetResult")
        Thread.sleep(100)

        // Step 3: Clear HALT on bulk endpoints
        val clearOut = connection!!.controlTransfer(
            0x02, 0x01, 0, bulkOutEndpoint!!.address, null, 0, 2000
        )
        Log.d(
            PtpConstants.LOG_TAG,
            "Clear HALT on bulkOut (addr=${bulkOutEndpoint!!.address}): $clearOut"
        )

        val clearIn = connection!!.controlTransfer(
            0x02, 0x01, 0, bulkInEndpoint!!.address, null, 0, 2000
        )
        Log.d(
            PtpConstants.LOG_TAG,
            "Clear HALT on bulkIn (addr=${bulkInEndpoint!!.address}): $clearIn"
        )

        // Step 4: Drain stale data from bulk IN (leftovers from MTP service
        // probing, or a previously-interrupted transfer).
        val drainBuf = ByteArray(512)
        var drained = 0
        while (true) {
            val read = connection!!.bulkTransfer(bulkInEndpoint!!, drainBuf, drainBuf.size, 200)
            if (read <= 0) break
            drained += read
        }
        if (drained > 0) Log.d(PtpConstants.LOG_TAG, "Drained $drained stale bytes from bulk IN")

        // Step 5: Short settle so the camera's PTP state machine is ready
        // to accept OpenSession.
        Thread.sleep(500)
    }

    fun clearEndpoints() {
        val clearOut =
            connection!!.controlTransfer(0x02, 0x01, 0, bulkOutEndpoint!!.address, null, 0, 2000)
        val clearIn =
            connection!!.controlTransfer(0x02, 0x01, 0, bulkInEndpoint!!.address, null, 0, 2000)
        Log.d(PtpConstants.LOG_TAG, "Clear endpoints: out=$clearOut, in=$clearIn")
        // Drain anything left (reentrant — this re-enters the lock we already hold)
        val buf = ByteArray(512)
        var flushed = 0
        while (true) {
            val read = connection!!.bulkTransfer(bulkInEndpoint!!, buf, buf.size, 100)
            if (read <= 0) break
            flushed += read
        }
        if (flushed > 0) {
            Log.d(PtpConstants.LOG_TAG, "Flushed $flushed stale bytes from bulk IN pipe")
        }
    }
}