package cn.ykcryobs.ptportal.ptp

import android.util.Log
import cn.ykcryobs.ptportal.ptp.constants.PtpConstants
import cn.ykcryobs.ptportal.ptp.constants.PtpResponseCode
import cn.ykcryobs.ptportal.ptp.constants.PtpStandardOpCode
import cn.ykcryobs.ptportal.ptp.model.ExtDeviceInfoResult
import cn.ykcryobs.ptportal.ptp.model.IsEnabled
import cn.ykcryobs.ptportal.ptp.model.SdiExtDevicePropInfo
import cn.ykcryobs.ptportal.ptp.parser.SdioExtDevicePropInfoParser
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SdioHandshake(private val session: PtpSession) {

    fun sdioConnect(phase: Int): Boolean {
        val (resp) = session.sendCommandWithDataIn(
            PtpStandardOpCode.SDIO_CONNECT, phase, 0x00, 0x00
        )
        return if (resp == PtpResponseCode.OK) {
            Log.i(PtpConstants.LOG_TAG, "SDIO 连接成功，phase=$phase")
            true
        } else {
            Log.e(PtpConstants.LOG_TAG, "SDIO 连接失败，phase=$phase，响应=$resp")
            false
        }
    }

    fun sdioGetExtDeviceInfo(version: Int, propertyFlag: Int = 0): ExtDeviceInfoResult {
        val (resp, _, data) = if (propertyFlag != 0) {
            session.sendCommandWithDataIn(
                PtpStandardOpCode.SDIO_GET_EXT_DEVICE_INFO, version, propertyFlag
            )
        } else {
            session.sendCommandWithDataIn(
                PtpStandardOpCode.SDIO_GET_EXT_DEVICE_INFO, version
            )
        }
        if (resp != PtpResponseCode.OK) {
            Log.e(PtpConstants.LOG_TAG, "GetDeviceInfo 失败, resp=$resp")
            return ExtDeviceInfoResult.ResponseError
        }

        // NOTE 暂时只解析第一个参数，第二三个参数组丢弃
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        if (buffer.remaining() < 2) {
            Log.e(PtpConstants.LOG_TAG, "GetDeviceInfo 返回数据过短: ${buffer.remaining()} bytes")
            return ExtDeviceInfoResult.DataTooShort
        }
        val sdioVer = buffer.short.toInt() and 0xFFFF
        Log.d(PtpConstants.LOG_TAG, "SDIExtensionVersion = 0x%04X".format(sdioVer))

        // NOTE 要求 版本为 0x12c（3.0.0）
        return if (sdioVer == 0x12c) {
            ExtDeviceInfoResult.Success(sdioVer)
        } else {
            ExtDeviceInfoResult.VersionMismatch(expected = 0x12c, actual = sdioVer)
        }
    }

    fun getVendorCodeVersion(): Int {
        val (resp, _, params) = session.sendCommand(PtpStandardOpCode.SDIO_GET_VENDOR_CODE_VERSION)
        if (resp != PtpResponseCode.OK) {
            Log.e(PtpConstants.LOG_TAG, "GetDeviceInfo 失败")
            return -1
        }
        return params[0]
    }

    fun getAllExtDevicePropInfo(
        onlyDifference: Boolean = false, enableExtendedProps: Boolean = true
    ): SdiExtDevicePropInfo? {
        val flag1 = if (onlyDifference) 0x01 else 0x00
        val flag2 = if (enableExtendedProps) 0x01 else 0x00
        val (resp, _, data) = session.sendCommandWithDataIn(
            PtpStandardOpCode.SDIO_GET_ALL_EXT_DEVICE_PROP_INFO, flag1, flag2
        )
        if (resp != PtpResponseCode.OK) {
            Log.e(PtpConstants.LOG_TAG, "SDIO_GetAllExtDevicePropInfo 失败, resp=$resp")
            return null
        }

        val result = SdioExtDevicePropInfoParser.parse(data)
        if (result == null) {
            Log.e(PtpConstants.LOG_TAG, "SDIO_GetAllExtDevicePropInfo 解析失败")
        } else {
            // TODO 临时打印，检查完删除
            Log.d(
                PtpConstants.LOG_TAG,
                "=== GetAllExtDevicePropInfo (${result.properties.size} 个属性) ==="
            )
            for (prop in result.properties) {
                val enableStr = when (prop.isEnabled) {
                    IsEnabled.INVALID -> "invalid"
                    IsEnabled.VALID -> "valid"
                    IsEnabled.DISPLAY_ONLY -> "displayOnly"
                }
                Log.d(
                    PtpConstants.LOG_TAG,
                    "Prop 0x%04X | type=0x%04X | %s | %s | current=0x%X | default=0x%X | set=%d | getSet=%d".format(
                        prop.propertyCode,
                        prop.dataType,
                        if (prop.isSettable) "RW" else "RO",
                        enableStr,
                        prop.currentValue,
                        prop.defaultValue,
                        prop.setValues.size,
                        prop.getSetValues.size,
                    )
                )
            }
        }
        return result
    }

    fun performFullHandshake(): Boolean {
        if (!sdioConnect(0x01)) {
            Log.e(PtpConstants.LOG_TAG, "SDIO phase01握手失败")
            return false
        }

        if (!sdioConnect(0x02)) {
            Log.e(PtpConstants.LOG_TAG, "SDIO phase02握手失败")
            return false
        }

        val maxRetry = 5
        var extInfoResult: ExtDeviceInfoResult = ExtDeviceInfoResult.ResponseError
        var retryCount = 0
        while (retryCount < maxRetry) {
            retryCount++
            Log.d(PtpConstants.LOG_TAG, "sdioGetExtDeviceInfo 第 $retryCount/$maxRetry 次尝试")
            extInfoResult = sdioGetExtDeviceInfo(0x12c)
            if (extInfoResult is ExtDeviceInfoResult.Success) break
            if (retryCount < maxRetry) Thread.sleep(100)
        }

        if (extInfoResult !is ExtDeviceInfoResult.Success) {
            if (extInfoResult is ExtDeviceInfoResult.VersionMismatch) {
                // TODO 提示当前 SDIO扩展版本 不是受支持的 v3 版本
                Log.e(
                    PtpConstants.LOG_TAG,
                    "不受支持的 SDIO 版本：v${extInfoResult.actual}，仅支持版本：v${extInfoResult.expected}"
                )
            } else {
                Log.e(
                    PtpConstants.LOG_TAG,
                    "sdioGetExtDeviceInfo 获取SDIO扩展信息失败（重试 $maxRetry 次），握手终止: $extInfoResult"
                )
            }
            return false
        }



        if (!sdioConnect(0x03)) {
            Log.e(PtpConstants.LOG_TAG, "SDIO phase03握手失败")
            return false
        }

        val vendorCode = getVendorCodeVersion()
        Log.d(PtpConstants.LOG_TAG, "SDIO vendorCodeVersion=$vendorCode")
        when {
            vendorCode >= 310 -> {
                if (vendorCode > 310) {
                    // TODO 提示当前 SDIO扩展版本 高于 v3.10，尝试运行在 v3.10 版本
                    Log.w(
                        PtpConstants.LOG_TAG,
                        "当前 SDIO 扩展版本 ($vendorCode) 高于 v3.10，尝试运行在 v3.10 模式"
                    )
                }
                val extInfoOk = sdioGetExtDeviceInfo(0x12c, 0x01)
                if (extInfoOk !is ExtDeviceInfoResult.Success) {
                    Log.e(PtpConstants.LOG_TAG, "SDIO 扩展版本声明失败: $extInfoOk")
                    return false
                }
            }

            else -> {
                // TODO 提示当前 SDIO扩展版本 低于 v3.10，可能出现兼容性问题
                Log.w(
                    PtpConstants.LOG_TAG,
                    "当前 SDIO 扩展版本 ($vendorCode) 低于 v3.10，可能出现兼容性问题"
                )
            }
        }

        Log.i(PtpConstants.LOG_TAG, "PTP+SDIO完整初始化完成")

        val enableExt = vendorCode >= 310
        getAllExtDevicePropInfo(enableExt)

        return true
    }
}
