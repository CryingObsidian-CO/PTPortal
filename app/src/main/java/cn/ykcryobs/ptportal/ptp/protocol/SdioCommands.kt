package cn.ykcryobs.ptportal.ptp.protocol

import android.util.Log
import cn.ykcryobs.ptportal.ptp.common.PtpConstants
import cn.ykcryobs.ptportal.ptp.codec.constants.DisplayStringListType
import cn.ykcryobs.ptportal.ptp.codec.constants.PtpResponseCode
import cn.ykcryobs.ptportal.ptp.codec.constants.PtpStandardOpCode
import cn.ykcryobs.ptportal.ptp.codec.constants.SdioPropCode
import cn.ykcryobs.ptportal.ptp.codec.model.DevicePropInfo
import cn.ykcryobs.ptportal.ptp.codec.model.ExtDeviceInfoResult
import cn.ykcryobs.ptportal.ptp.codec.model.IsEnabled
import cn.ykcryobs.ptportal.ptp.codec.model.SdioDisplayStringList
import cn.ykcryobs.ptportal.ptp.codec.model.SdioExtDevicePropInfo
import cn.ykcryobs.ptportal.ptp.codec.parser.SdioDisplayStringListParser
import cn.ykcryobs.ptportal.ptp.codec.parser.SdioExtDevicePropInfoParser
import cn.ykcryobs.ptportal.ptp.core.PtpSession
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * SDIO 扩展协议命令层：一次调用 = 一条命令，不包含任何业务编排/流程。
 */
class SdioCommands(private val session: PtpSession) {

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
        Log.d(PtpConstants.LOG_TAG, "SDIOExtensionVersion = 0x%04X".format(sdioVer))

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
    ): SdioExtDevicePropInfo? {
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
            for (prop in result.properties.sortedBy { it.propertyCode }) {
                val enableStr = when (prop.isEnabled) {
                    IsEnabled.INVALID -> "invalid"
                    IsEnabled.VALID -> "valid"
                    IsEnabled.DISPLAY_ONLY -> "displayOnly"
                }
                val propCode = SdioPropCode.fromCode(prop.propertyCode)
                Log.d(
                    PtpConstants.LOG_TAG,
                    "Prop %s | type=0x%04X | %s | %s | current=%s | default=%s | form=%s".format(
                        propCode,
                        prop.dataType,
                        if (prop.isSettable) "RW" else "RO",
                        enableStr,
                        propCode.labelOf(prop.currentValue),
                        propCode.labelOf(prop.defaultValue),
                        prop.form,
                    )
                )
            }
        }
        return result
    }

    fun getExtDevicePropInfo(propCode: Int): DevicePropInfo? {
        val (resp, _, data) = session.sendCommandWithDataIn(
            PtpStandardOpCode.SDIO_GET_EXT_DEVICE_PROP, propCode
        )
        if (resp != PtpResponseCode.OK) {
            Log.e(
                PtpConstants.LOG_TAG,
                "SDIO_GetExtDeviceProp 失败, propCode=0x${propCode.toString(16)}, resp=$resp"
            )
            return null
        }
        val prop = SdioExtDevicePropInfoParser.parseSingle(data)
        if (prop == null) {
            Log.e(
                PtpConstants.LOG_TAG,
                "SDIO_GetExtDeviceProp 解析失败, propCode=0x${propCode.toString(16)}"
            )
        } else {
            Log.d(
                PtpConstants.LOG_TAG,
                "SDIO_GetExtDeviceProp 成功: propCode=0x${propCode.toString(16)}, current=${prop.currentValue}"
            )
        }
        return prop
    }

    fun getDisplayStringList(type: DisplayStringListType): SdioDisplayStringList? {
        val (resp, _, data) = session.sendCommandWithDataIn(
            PtpStandardOpCode.SDIO_GET_DISPLAY_STRING_LIST, type.code
        )
        if (resp != PtpResponseCode.OK) {
            Log.e(PtpConstants.LOG_TAG, "SDIO_GetDisplayStringList 失败, type=${type}, resp=$resp")
            return null
        }

        val stringList = SdioDisplayStringListParser.parse(data)
        if (stringList == null) {
            Log.e(PtpConstants.LOG_TAG, "SDIO_GetDisplayStringList 解析失败")
            return null
        }

        Log.d(
            PtpConstants.LOG_TAG,
            "SDIO_GetDisplayStringList 成功: type=$type, strings=${stringList.displayStringList.size}"
        )
        return stringList
    }

    fun setContentsTransferMode(
        contentsSelectType: Int = 0x01, // Select on the Camera
        transferMode: Int = 0x01,       // On
        additionalInfo: Int = 0x00,     // None
    ): Boolean {
        val (resp) = session.sendCommand(
            PtpStandardOpCode.SDIO_SET_CONTENTS_TRANSFER_MODE,
            contentsSelectType,
            transferMode,
            additionalInfo
        )
        return if (resp == PtpResponseCode.OK) {
            Log.i(
                PtpConstants.LOG_TAG,
                "SDIO_SetContentsTransferMode 成功: selectType=0x%X, mode=0x%X".format(
                    contentsSelectType, transferMode
                )
            )
            true
        } else {
            Log.e(
                PtpConstants.LOG_TAG, "SDIO_SetContentsTransferMode 失败: resp=$resp"
            )
            false
        }
    }
}