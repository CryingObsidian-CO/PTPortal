package cn.ykcryobs.ptportal.di

import android.content.Context
import cn.ykcryobs.ptportal.data.connection.UsbCameraConnectionRepository
import cn.ykcryobs.ptportal.data.preferences.SharedPreferencesUserPreferencesRepository
import cn.ykcryobs.ptportal.domain.connection.CameraConnectionRepository
import cn.ykcryobs.ptportal.domain.preferences.UserPreferencesRepository
import cn.ykcryobs.ptportal.domain.remote.CameraControlRepository
import cn.ykcryobs.ptportal.mock.MockCameraControlRepository

/**
 * 手动依赖注入容器：聚合所有「数据层」实现，UI 层只面向 domain 接口。
 * 需要替换/注入 Mock 时，只改这里。
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    /** 相机连接仓库（持有生命周期，供 Activity 调用 start/release）。 */
    val usbCameraRepository: UsbCameraConnectionRepository by lazy {
        UsbCameraConnectionRepository(appContext)
    }

    val connectionRepository: CameraConnectionRepository
        get() = usbCameraRepository

    val preferencesRepository: UserPreferencesRepository by lazy {
        SharedPreferencesUserPreferencesRepository(appContext)
    }

    val cameraControlRepository: CameraControlRepository by lazy {
        MockCameraControlRepository()
    }

    fun release() {
        usbCameraRepository.release()
    }
}