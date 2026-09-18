package cn.ykcryobs.ptportal.ptp.service

import android.util.Log
import cn.ykcryobs.ptportal.ptp.common.PtpConstants
import cn.ykcryobs.ptportal.ptp.codec.model.ExtDeviceInfoResult
import cn.ykcryobs.ptportal.ptp.protocol.SdioCommands

/**
 * SDIO 握手/初始化流程编排：承载 phase 状态机、版本协商与重试策略，
 * 仅依赖命令层 SdioCommands，不直接接触 session/字节流。
 */
class SdioHandshakeService(private val commands: SdioCommands) {

    fun performFullHandshake(): Boolean {
        if (!commands.sdioConnect(0x01)) {
            Log.e(PtpConstants.LOG_TAG, "SDIO phase01握手失败")
            return false
        }

        if (!commands.sdioConnect(0x02)) {
            Log.e(PtpConstants.LOG_TAG, "SDIO phase02握手失败")
            return false
        }

        val maxRetry = 5
        var extInfoResult: ExtDeviceInfoResult = ExtDeviceInfoResult.ResponseError
        var retryCount = 0
        while (retryCount < maxRetry) {
            retryCount++
            Log.d(PtpConstants.LOG_TAG, "sdioGetExtDeviceInfo 第 $retryCount/$maxRetry 次尝试")
            extInfoResult = commands.sdioGetExtDeviceInfo(0x12c)
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

        if (!commands.sdioConnect(0x03)) {
            Log.e(PtpConstants.LOG_TAG, "SDIO phase03握手失败")
            return false
        }

        val vendorCode = commands.getVendorCodeVersion()
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
                val extInfoOk = commands.sdioGetExtDeviceInfo(0x12c, 0x01)
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
        commands.getAllExtDevicePropInfo(enableExt)

        return true
    }
}