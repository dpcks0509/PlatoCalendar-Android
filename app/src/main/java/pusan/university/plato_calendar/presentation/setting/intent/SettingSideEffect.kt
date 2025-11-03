package pusan.university.plato_calendar.presentation.setting.intent

import pusan.university.plato_calendar.presentation.common.base.UiSideEffect

sealed interface SettingSideEffect : UiSideEffect {
    data class NavigateToWebView(
        val url: String,
    ) : SettingSideEffect
}
