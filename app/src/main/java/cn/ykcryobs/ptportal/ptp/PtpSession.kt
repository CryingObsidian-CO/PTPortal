package cn.ykcryobs.ptportal.ptp

import android.util.Log
import cn.ykcryobs.ptportal.ptp.constants.PtpConstants
import cn.ykcryobs.ptportal.ptp.constants.PtpContainerType
import cn.ykcryobs.ptportal.ptp.constants.PtpResponseCode
import cn.ykcryobs.ptportal.ptp.constants.PtpStandardOpCode
import cn.ykcryobs.ptportal.ptp.constants.PtpEventCode
import cn.ykcryobs.ptportal.ptp.constants.SdioPropCode
import cn.ykcryobs.ptportal.ptp.model.IsEnabled
import cn.ykcryobs.ptportal.ptp.model.PtpDataResponse
import cn.ykcryobs.ptportal.ptp.model.PtpResponse
import cn.ykcryobs.ptportal.ptp.model.PtpEvent
import cn.ykcryobs.ptportal.ptp.model.ParsedStorageInfo
import cn.ykcryobs.ptportal.ptp.parser.SdioExtDevicePropInfoParser
import cn.ykcryobs.ptportal.ptp.parser.StorageInfoParser
import cn.ykcryobs.ptportal.usb.UsbTransport
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.withLock

class PtpSession(val transport: UsbTransport) {

    private val transactionId = AtomicInteger(1)

    fun openSession(): Boolean {
        var resp = sendCommandWithDataIn(PtpStandardOpCode.SDIO_OPEN_SESSION, 0x01, 0x02)
//        var resp = sendCommand(PtpStandardOpCode.OPEN_SESSION, 0x01)
        if (resp.respCode == PtpResponseCode.SESSION_ALREADY_OPEN) {
            Log.w(PtpConstants.LOG_TAG, "检测到旧 session 未关闭，先关闭再重试")
            closeSession()
            resp = sendCommandWithDataIn(PtpStandardOpCode.SDIO_OPEN_SESSION, 0x01, 0x02)
//            resp = sendCommand(PtpStandardOpCode.OPEN_SESSION, 0x01)
        }
        return if (resp.respCode == PtpResponseCode.OK) {
            Log.i(PtpConstants.LOG_TAG, "PTP 会话打开成功")
            true
        } else {
            Log.e(PtpConstants.LOG_TAG, "PTP 会话打开失败，响应=$resp")
            false
        }
    }

    fun closeSession() {
        try {
            sendCommand(PtpStandardOpCode.CLOSE_SESSION, 0x01)
        } catch (_: Exception) {
        }
        transactionId.set(1)
    }

    fun sendCommand(opCode: PtpStandardOpCode, vararg params: Int): PtpResponse =
        transport.lock.withLock {
            val tId = sendCommandOnly(opCode, *params)
            if (tId == -1) {
                return@withLock PtpResponse(PtpResponseCode.GENERAL_ERROR, -1)
            }
            readResponse(tId)
        }

    fun sendCommandWithDataIn(opCode: PtpStandardOpCode, vararg params: Int): PtpDataResponse =
        transport.lock.withLock {
            val tId = sendCommandOnly(opCode, *params)
            readDataAndResponse(tId)
        }

    private fun sendCommandOnly(opCode: PtpStandardOpCode, vararg params: Int): Int {
        val tId = transactionId.getAndIncrement()
        val paramCount = params.size
        val totalLen = PtpConstants.HEADER_SIZE + paramCount * 4

        val buffer = ByteBuffer.allocate(totalLen).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putInt(totalLen)
        buffer.putShort(PtpContainerType.COMMAND.getShort())
        buffer.putShort(opCode.getShort())
        buffer.putInt(tId)
        params.forEach { buffer.putInt(it) }

        val sendLen = transport.bulkTransferOut(buffer.array(), totalLen)
        if (sendLen != totalLen) {
            Log.e(PtpConstants.LOG_TAG, "命令发送失败: 期望$totalLen 实际$sendLen")
            return -1
        }

        Log.d(
            PtpConstants.LOG_TAG,
            "发送命令: opCode=0x${opCode.code.toString(16).padStart(4, '0')}, tId=$tId"
        )
        Log.d(
            PtpConstants.LOG_TAG,
            "命令包字节: ${buffer.array().joinToString(" ") { "%02x".format(it) }}"
        )
        Log.d(PtpConstants.LOG_TAG, "bulkTransfer(OUT) 返回: $sendLen (期望 $totalLen)")

        return tId
    }

    private fun readResponse(
        expectedTId: Int, timeoutMs: Int = PtpConstants.USB_TIMEOUT
    ): PtpResponse {
        val buffer = ByteArray(PtpConstants.HEADER_SIZE + PtpConstants.RESPONSE_MAX_PARAMS)
        val ret = transport.bulkTransferIn(buffer, timeoutMs)

        if (ret < PtpConstants.HEADER_SIZE) {
            Log.e(PtpConstants.LOG_TAG, "读取响应包失败: 期望：>=12 实际：$ret")
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
                val remaining = length - ret
                if (remaining > 0) {
                    val buf = ByteArray(PtpConstants.USB_TRANSFER_BUFFER)
                    var left = remaining
                    while (left > 0) {
                        val read = transport.bulkTransferIn(buf)
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
        val ret = transport.bulkTransferIn(headerBuf)

        if (ret < PtpConstants.HEADER_SIZE) {
            Log.e(PtpConstants.LOG_TAG, "读取响应包失败: 期望 >=12 实际$ret")
            return PtpDataResponse(PtpResponseCode.GENERAL_ERROR, tId, ByteArray(0))
        }

        val buffer = ByteBuffer.wrap(headerBuf, 0, ret).order(ByteOrder.LITTLE_ENDIAN)
        val totalLength = buffer.getInt()
        val type = buffer.getShort().toInt() and 0xFFFF
        val code = buffer.getShort().toInt() and 0xFFFF
        val respTId = buffer.getInt()

        if (type == PtpContainerType.RESPONSE.code) {
            return PtpDataResponse(code, respTId, ByteArray(0))
        }
        if (type != PtpContainerType.DATA.code) {
            Log.w(PtpConstants.LOG_TAG, "意外的包头，期望 data，实际：$type")
            return PtpDataResponse(PtpResponseCode.GENERAL_ERROR, respTId, ByteArray(0))
        }

        val dataSize = totalLength - PtpConstants.HEADER_SIZE
        val output = ByteArrayOutputStream(dataSize.coerceAtMost(PtpConstants.USB_TRANSFER_BUFFER))

        val firstChunkSize = ret - PtpConstants.HEADER_SIZE
        if (firstChunkSize > 0) {
            output.write(headerBuf, PtpConstants.HEADER_SIZE, firstChunkSize)
        }

        var totalRead = firstChunkSize
        while (totalRead < dataSize) {
            val chunkRead = transport.bulkTransferIn(headerBuf)
            if (chunkRead <= 0) break
            output.write(headerBuf, 0, chunkRead)
            totalRead += chunkRead
        }

        val data = output.toByteArray()
        val response = readResponse(respTId)

        return PtpDataResponse(response.respCode, respTId, data)
    }

    fun getDeviceInfo(): ByteArray? {
        val (resp, _, data) = sendCommandWithDataIn(PtpStandardOpCode.GET_DEVICE_INFO)
        if (resp != PtpResponseCode.OK) {
            Log.e(PtpConstants.LOG_TAG, "GetDeviceInfo 失败")
            return null
        }
        return data
    }

    fun getStorageIds(): List<Int>? {
        val (resp, _, data) = sendCommandWithDataIn(PtpStandardOpCode.GET_STORAGE_IDS)
        if (resp != PtpResponseCode.OK) {
            Log.e(PtpConstants.LOG_TAG, "GetStorageIDs 失败: $resp")
        }
        Log.d(PtpConstants.LOG_TAG, data.contentToString())
        return StorageInfoParser.parseStorageIds(data)
    }

    fun getStorageInfo(storageId: Int): ParsedStorageInfo? {
        val (resp, _, data) = sendCommandWithDataIn(PtpStandardOpCode.GET_STORAGE_INFO, storageId)
        if (resp != PtpResponseCode.OK) {
            Log.e(
                PtpConstants.LOG_TAG,
                "GetStorageInfo 失败: storageId=0x${storageId.toString(16)}, $resp"
            )
            return null
        }
        return StorageInfoParser.parse(data)
    }


    fun readEvent(timeoutMs: Int = PtpConstants.EVENT_TIMEOUT): PtpEvent? {
        val buffer = ByteArray(PtpConstants.EVENT_BUFFER_SIZE)
        val ret = transport.interruptTransferIn(buffer, timeoutMs)
        if (ret < PtpConstants.HEADER_SIZE) {
            return null
        }
        val buf = ByteBuffer.wrap(buffer, 0, ret).order(ByteOrder.LITTLE_ENDIAN)
        val length = buf.getInt()
        val type = buf.getShort().toInt() and 0xFFFF
        val eventCode = buf.getShort().toInt() and 0xFFFF
        val transactionId = buf.getInt()
        val paramCount = (ret - PtpConstants.HEADER_SIZE) / 4
        val params = List(paramCount) { buf.getInt() }

        if (type != PtpContainerType.EVENT.code) {
            Log.w(PtpConstants.LOG_TAG, "中断端点收到非事件包，type=$type")
            return null
        }
        return PtpEvent(eventCode, transactionId, params)
    }

    fun waitForStoreAdded(timeoutMs: Int = 500000): Boolean {
        if (transport.interruptInEndpoint == null) {
            Log.w(PtpConstants.LOG_TAG, "没有中断端点，无法监听 StoreAdded 事件")
            return false
        }
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            val event = readEvent() ?: continue
            Log.d(
                PtpConstants.LOG_TAG,
                "收到 PTP 事件: code=0x%04X, params=%s".format(event.eventCode, event.params)
            )
            when (event.eventCode) {
                PtpEventCode.STORE_ADDED -> {
                    Log.i(
                        PtpConstants.LOG_TAG, "收到 StoreAdded 事件，StorageID=0x%08X".format(
                            event.params.firstOrNull() ?: 0
                        )
                    )
                    return true
                }

                else -> Log.d(
                    PtpConstants.LOG_TAG, "收到未处理事件: 0x%04X".format(event.eventCode)
                )
            }
        }
        Log.w(PtpConstants.LOG_TAG, "等待 StoreAdded 事件超时（${timeoutMs}ms）")
        return false
    }

}
