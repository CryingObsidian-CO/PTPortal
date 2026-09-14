package cn.ykcryobs.ptportal.usb

import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.util.Log
import cn.ykcryobs.ptportal.AppContextHolder
import cn.ykcryobs.ptportal.ptp.constants.PtpConstants
import cn.ykcryobs.ptportal.ptp.EventManager
import cn.ykcryobs.ptportal.ptp.constants.PtpContainerType
import cn.ykcryobs.ptportal.ptp.model.RawPtpEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class UsbTransport {

    val lock = ReentrantLock()

    @Volatile
    var connection: UsbDeviceConnection? = null
        private set

    @Volatile
    var bulkOutEndpoint: UsbEndpoint? = null
        private set

    @Volatile
    var bulkInEndpoint: UsbEndpoint? = null
        private set

    @Volatile
    var interruptInEndpoint: UsbEndpoint? = null
        private set

    private var interruptJob: Job? = null
    private var ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var ptpInterface: UsbInterface? = null

    fun openDevice(device: UsbDevice): Boolean = lock.withLock {
        close()

        val usbManager = AppContextHolder.usbManager
        val conn = usbManager.openDevice(device) ?: run {
            Log.e(PtpConstants.LOG_TAG, "openDevice 返回null，没有权限或者设备被占用")
            return false
        }
        connection = conn

        for (interfaceIndex in 0 until device.interfaceCount) {
            val iface = device.getInterface(interfaceIndex)
            if (iface.interfaceClass == PtpConstants.USB_CLASS_PTP && iface.interfaceSubclass == PtpConstants.USB_SUBCLASS_PTP) {
                ptpInterface = iface
                if (!conn.claimInterface(iface, true)) {
                    Log.e(PtpConstants.LOG_TAG, "claimInterface失败")
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
                    } else if (ep.type == UsbConstants.USB_ENDPOINT_XFER_INT && ep.direction == UsbConstants.USB_DIR_IN) {
                        interruptInEndpoint = ep
                    }
                }
            }
        }

        if (bulkInEndpoint == null || bulkOutEndpoint == null || interruptInEndpoint == null) {
            Log.e(PtpConstants.LOG_TAG, "找不到PTP Bulk端点")
            close()
            return false
        }
        Log.i(PtpConstants.LOG_TAG, "PTP端点查找成功")
        true
    }

    fun startInterruptListener() {
        if (interruptJob?.isActive == true) return
        if (ioScope.isActive.not()) {
            ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        }
        interruptJob = ioScope.launch {
            val buffer = ByteArray(PtpConstants.EVENT_BUFFER_SIZE)
            while (isActive) {
                try {
                    val ret = interruptTransferIn(buffer)
                    if (ret < PtpConstants.HEADER_SIZE) {
//                        Log.e(PtpConstants.LOG_TAG, "读取事件包失败: 期望：>=12 实际：$ret")
                        continue
                    }

                    val respBuf = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN)
                    respBuf.getInt()
                    val type = respBuf.getShort().toInt() and 0xFFFF
                    val code = respBuf.getShort().toInt() and 0xFFFF
                    respBuf.getInt()

                    if (type != PtpContainerType.EVENT.code) {
                        Log.e(PtpConstants.LOG_TAG, "非事件包通过中断口发送")
                        continue
                    }

                    val paramCount = (ret - PtpConstants.HEADER_SIZE) / 4
                    val params = IntArray(paramCount) { respBuf.getInt() }
                    Log.d(PtpConstants.LOG_TAG, "eventCode:$code")
                    EventManager.instance.postEvent(RawPtpEvent(code, params))
                } catch (ex: Exception) {
                    Log.e(PtpConstants.LOG_TAG, "aaaa$ex")
                    break
                }
            }
        }
    }

    fun stopInterruptListener() {
        interruptJob?.cancel()
        interruptJob = null
    }

    fun close() = lock.withLock {

        stopInterruptListener()
        ioScope.cancel()

        val connSnap = connection
        val ifaceSnap = ptpInterface

        if (connSnap != null && ifaceSnap != null) {
            try {
                connSnap.releaseInterface(ifaceSnap)
                Log.i(PtpConstants.LOG_TAG, "close: releaseInterface ok")
            } catch (e: Exception) {
                Log.w(PtpConstants.LOG_TAG, "close: releaseInterface exception", e)
            }
        }

        connSnap?.close()
        Log.i(PtpConstants.LOG_TAG, "close: connection closed")

        connection = null
        bulkInEndpoint = null
        bulkOutEndpoint = null
        interruptInEndpoint = null
        ptpInterface = null
    }

    fun bulkTransferOut(data: ByteArray, length: Int): Int = lock.withLock {
        connection?.bulkTransfer(bulkOutEndpoint, data, length, PtpConstants.USB_TIMEOUT) ?: -1
    }

    fun bulkTransferIn(buffer: ByteArray, timeoutMs: Int = PtpConstants.USB_TIMEOUT): Int =
        lock.withLock {
            connection?.bulkTransfer(bulkInEndpoint, buffer, buffer.size, timeoutMs) ?: -1
        }

    fun interruptTransferIn(buffer: ByteArray, timeoutMs: Int = PtpConstants.EVENT_TIMEOUT): Int =
        lock.withLock {
            connection?.bulkTransfer(interruptInEndpoint, buffer, buffer.size, timeoutMs) ?: -1
        }
}
