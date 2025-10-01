package pnu.plato.calendar.presentation.setting

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pnu.plato.calendar.domain.entity.LoginCredentials
import pnu.plato.calendar.domain.entity.LoginStatus
import pnu.plato.calendar.presentation.common.base.BaseViewModel
import pnu.plato.calendar.presentation.common.manager.LoginManager
import pnu.plato.calendar.presentation.setting.intent.SettingEvent
import pnu.plato.calendar.presentation.setting.intent.SettingEvent.HideLoginDialog
import pnu.plato.calendar.presentation.setting.intent.SettingEvent.ShowLoginDialog
import pnu.plato.calendar.presentation.setting.intent.SettingSideEffect
import pnu.plato.calendar.presentation.setting.intent.SettingState
import javax.inject.Inject

@HiltViewModel
class SettingViewModel
    @Inject
    constructor(
        val loginManager: LoginManager,
    ) : BaseViewModel<SettingState, SettingEvent, SettingSideEffect>(SettingState()) {
        init {
            viewModelScope.launch {
                loginManager.loginStatus.collect { loginStatus ->
                    when (loginStatus) {
                        is LoginStatus.Login -> {
                            setState { copy(userInfo = loginStatus.loginSession.userInfo) }
                        }

                        else -> setState { copy(userInfo = null) }
                    }
                }
            }
        }

        override suspend fun handleEvent(event: SettingEvent) {
            when (event) {
                ShowLoginDialog -> setState { copy(isLoginDialogVisible = true) }
                HideLoginDialog -> setState { copy(isLoginDialogVisible = false) }
                SettingEvent.Logout -> {
                    loginManager.logout()
                }
            }
        }

        suspend fun tryLogin(credentials: LoginCredentials): Boolean {
            val isLoginSuccess = loginManager.login(credentials)
            if (isLoginSuccess) setState { copy(isLoginDialogVisible = false) }

            return isLoginSuccess
        }
    }
