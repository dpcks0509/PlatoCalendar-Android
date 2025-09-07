package pnu.plato.calendar.presentation.common.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import pnu.plato.calendar.data.local.database.LoginCredentialsDataStore
import pnu.plato.calendar.domain.entity.LoginCredentials
import pnu.plato.calendar.domain.entity.LoginStatus
import pnu.plato.calendar.domain.repository.LoginRepository
import pnu.plato.calendar.presentation.common.eventbus.ErrorEventBus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginManager
    @Inject
    constructor(
        private val loginRepository: LoginRepository,
        private val preferences: LoginCredentialsDataStore,
    ) {
        private val _loginStatus = MutableStateFlow<LoginStatus>(LoginStatus.Logout)
        val loginStatus: StateFlow<LoginStatus> = _loginStatus.asStateFlow()

        suspend fun autoLogin(): Boolean {
            val loginCredentials = preferences.loginCredentials.firstOrNull()

            loginCredentials?.let { loginCredentials ->
                if (loginStatus.value is LoginStatus.Logout) {
                    loginRepository
                        .login(loginCredentials)
                        .onSuccess { loginSession ->
                            _loginStatus.update { LoginStatus.Login(loginSession) }

                            return true
                        }.onFailure { throwable ->
                            ErrorEventBus.sendError(throwable.message)
                        }
                }
                return false
            }

            return false
        }

        suspend fun login(credentials: LoginCredentials): Boolean {
            if (loginStatus.value is LoginStatus.Logout) {
                loginRepository
                    .login(credentials)
                    .onSuccess { loginSession ->
                        _loginStatus.update { LoginStatus.Login(loginSession) }
                        preferences.saveLoginCredentials(credentials)

                        return true
                    }.onFailure { throwable ->
                        ErrorEventBus.sendError(throwable.message)
                    }
            }
            return false
        }

        suspend fun logout(): Boolean {
            val currentLoginStatus = loginStatus.value

            if (currentLoginStatus is LoginStatus.Login) {
                loginRepository
                    .logout(sessKey = currentLoginStatus.loginSession.sessKey)
                    .onSuccess {
                        _loginStatus.update { LoginStatus.Logout }
                        preferences.deleteLoginCredentials()

                        return true
                    }.onFailure { throwable ->
                        ErrorEventBus.sendError(throwable.message)
                    }
            }

            return false
        }
    }
