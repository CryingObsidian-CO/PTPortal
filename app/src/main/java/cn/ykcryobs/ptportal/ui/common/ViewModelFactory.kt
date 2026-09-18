package cn.ykcryobs.ptportal.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * 统一的 ViewModel Factory 构造工具：所有屏幕都通过它创建带依赖的 ViewModel，
 * 避免各屏幕重复定义 private factory。
 */
fun <VM : ViewModel> viewModelFactory(create: () -> VM): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <VM2 : ViewModel> create(modelClass: Class<VM2>): VM2 = create() as VM2
    }