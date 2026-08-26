package cn.ykcryobs.ptportal.domain.remote

import kotlinx.coroutines.flow.StateFlow

enum class ShootingMode { SingleShot, Continuous, Timer, Video }
enum class WhiteBalanceMode { Auto, Daylight, Shade, Cloudy, Tungsten, Fluorescent, Flash, Custom }
enum class FocusMode { AF, MF }

data class CameraSettings(
    val aperture: Float = 2.8f,
    val apertureOptions: List<Float> = listOf(1.4f, 1.8f, 2.0f, 2.8f, 4.0f, 5.6f, 8.0f, 11f, 16f),
    val shutterSpeed: String = "1/125",
    val shutterOptions: List<String> = listOf("1/4000", "1/2000", "1/1000", "1/500", "1/250", "1/125", "1/60", "1/30", "1/15"),
    val iso: Int = 400,
    val isoOptions: List<Int> = listOf(50, 100, 200, 400, 800, 1600, 3200, 6400, 12800),
    val exposureCompensation: Float = 0.0f,
    val whiteBalance: WhiteBalanceMode = WhiteBalanceMode.Auto,
    val shootingMode: ShootingMode = ShootingMode.SingleShot,
    val focusMode: FocusMode = FocusMode.AF,
)

sealed interface RecordingState {
    data object Idle : RecordingState
    data class Recording(val elapsedSeconds: Int) : RecordingState
}

interface CameraControlRepository {
    // TODO: 接实时流，返回帧数据或 Bitmap
    val liveViewActive: StateFlow<Boolean>
    val settings: StateFlow<CameraSettings>
    val isRecording: StateFlow<RecordingState>

    suspend fun capture(): Result<Unit> // TODO: 空实现，接 PTP 快门命令
    suspend fun startRecording(): Result<Unit> // TODO: 空实现
    suspend fun stopRecording(): Result<Unit> // TODO: 空实现
    suspend fun updateSetting(key: SettingKey, value: Any): Result<Unit> // TODO: 空实现
}

sealed interface SettingKey {
    data object Aperture : SettingKey
    data object ShutterSpeed : SettingKey
    data object Iso : SettingKey
    data object ExposureCompensation : SettingKey
    data object WhiteBalance : SettingKey
    data object ShootingMode : SettingKey
    data object FocusMode : SettingKey
}
