package cn.ykcryobs.ptportal.data.system

import android.app.LocaleManager
import android.content.Context
import android.os.LocaleList
import cn.ykcryobs.ptportal.domain.preferences.AppLanguage

/** 语言切换的系统级服务封装，UI 层只调用它，不直接接触 LocaleManager。 */
object AppLocaleManager {

    fun applyLanguage(context: Context, language: AppLanguage) {
        val localeManager = context.getSystemService(LocaleManager::class.java) ?: return
        localeManager.applicationLocales = when (language) {
            AppLanguage.System -> LocaleList.getEmptyLocaleList()
            AppLanguage.Chinese -> LocaleList.forLanguageTags("zh-CN")
            AppLanguage.English -> LocaleList.forLanguageTags("en")
        }
    }
}