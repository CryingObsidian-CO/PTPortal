package cn.ykcryobs.ptportal.data.system

import android.content.Context

/** 应用元信息读取。 */
fun Context.appVersionName(): String {
    return packageManager.getPackageInfo(packageName, 0).versionName ?: "--"
}