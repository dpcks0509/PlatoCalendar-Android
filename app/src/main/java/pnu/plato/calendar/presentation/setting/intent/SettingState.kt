package pnu.plato.calendar.presentation.setting.intent

import pnu.plato.calendar.presentation.common.base.UiState

data class SettingState(
    val userInfo: String? = null,
    val isLoginDialogVisible: Boolean = false,
) : UiState
