package pnu.plato.calendar.presentation.setting.intent

import pnu.plato.calendar.presentation.common.base.UiSideEffect

sealed interface SettingSideEffect : UiSideEffect {
    data class NavigateToWebView(
        val url: String,
    ) : SettingSideEffect
}
