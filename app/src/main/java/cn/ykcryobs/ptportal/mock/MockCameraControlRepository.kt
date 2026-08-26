package cn.ykcryobs.ptportal.mock

import cn.ykcryobs.ptportal.domain.remote.CameraControlRepository
import cn.ykcryobs.ptportal.domain.remote.CameraSettings
import cn.ykcryobs.ptportal.domain.remote.FocusMode
import cn.ykcryobs.ptportal.domain.remote.RecordingState
import cn.ykcryobs.ptportal.domain.remote.SettingKey
import cn.ykcryobs.ptportal.domain.remote.ShootingMode
import cn.ykcryobs.ptportal.domain.remote.WhiteBalanceMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MockCameraControlRepository : CameraControlRepository {

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var recordingJob: Job? = null

    private val _liveViewActive = MutableStateFlow(true)
    override val liveViewActive: StateFlow<Boolean> = _liveViewActive

    private val _settings = MutableStateFlow(CameraSettings())
    override val settings: StateFlow<CameraSettings> = _settings

    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.Idle)
    override val isRecording: StateFlow<RecordingState> = _recordingState

    override suspend fun capture(): Result<Unit> {
        delay(300) // simulate shutter lag
        return Result.success(Unit)
    }

    override suspend fun startRecording(): Result<Unit> {
        if (_recordingState.value is RecordingState.Recording) return Result.failure(IllegalStateException("Already recording"))
        _recordingState.value = RecordingState.Recording(0)
        recordingJob?.cancel()
        recordingJob = scope.launch {
            while (true) {
                delay(1000)
                val current = _recordingState.value
                if (current is RecordingState.Recording) {
                    _recordingState.value = current.copy(elapsedSeconds = current.elapsedSeconds + 1)
                } else break
            }
        }
        return Result.success(Unit)
    }

    override suspend fun stopRecording(): Result<Unit> {
        recordingJob?.cancel()
        _recordingState.value = RecordingState.Idle
        return Result.success(Unit)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun updateSetting(key: SettingKey, value: Any): Result<Unit> {
        val current = _settings.value
        _settings.value = when (key) {
            SettingKey.Aperture -> current.copy(aperture = value as Float)
            SettingKey.ShutterSpeed -> current.copy(shutterSpeed = value as String)
            SettingKey.Iso -> current.copy(iso = value as Int)
            SettingKey.ExposureCompensation -> current.copy(exposureCompensation = value as Float)
            SettingKey.WhiteBalance -> current.copy(whiteBalance = value as WhiteBalanceMode)
            SettingKey.ShootingMode -> current.copy(shootingMode = value as ShootingMode)
            SettingKey.FocusMode -> current.copy(focusMode = value as FocusMode)
        }
        delay(Random.nextLong(50, 150))
        return Result.success(Unit)
    }
}
