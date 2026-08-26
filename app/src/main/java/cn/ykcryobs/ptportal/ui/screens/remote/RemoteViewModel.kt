package cn.ykcryobs.ptportal.ui.screens.remote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.ykcryobs.ptportal.domain.remote.CameraControlRepository
import cn.ykcryobs.ptportal.domain.remote.CameraSettings
import cn.ykcryobs.ptportal.domain.remote.RecordingState
import cn.ykcryobs.ptportal.domain.remote.SettingKey
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RemoteViewModel(
    private val repository: CameraControlRepository,
) : ViewModel() {

    val liveViewActive: StateFlow<Boolean> = repository.liveViewActive
    val settings: StateFlow<CameraSettings> = repository.settings
    val recordingState: StateFlow<RecordingState> = repository.isRecording

    fun capture(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.capture()
            onDone()
        }
    }

    fun toggleRecording() {
        viewModelScope.launch {
            when (recordingState.value) {
                is RecordingState.Idle -> repository.startRecording()
                is RecordingState.Recording -> repository.stopRecording()
            }
        }
    }

    fun setAperture(value: Float) = update(SettingKey.Aperture, value)
    fun setShutterSpeed(value: String) = update(SettingKey.ShutterSpeed, value)
    fun setIso(value: Int) = update(SettingKey.Iso, value)
    fun setExposureCompensation(value: Float) = update(SettingKey.ExposureCompensation, value)
    fun setWhiteBalance(value: cn.ykcryobs.ptportal.domain.remote.WhiteBalanceMode) = update(SettingKey.WhiteBalance, value)
    fun setShootingMode(value: cn.ykcryobs.ptportal.domain.remote.ShootingMode) = update(SettingKey.ShootingMode, value)
    fun setFocusMode(value: cn.ykcryobs.ptportal.domain.remote.FocusMode) = update(SettingKey.FocusMode, value)

    private fun update(key: SettingKey, value: Any) {
        viewModelScope.launch { repository.updateSetting(key, value) }
    }
}
