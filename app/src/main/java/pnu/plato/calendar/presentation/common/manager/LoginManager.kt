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
        private val loginCredentialsDataStore: LoginCredentialsDataStore,
    ) {
        private val _loginStatus = MutableStateFlow<LoginStatus>(LoginStatus.Uninitialized)
        val loginStatus: StateFlow<LoginStatus> = _loginStatus.asStateFlow()

        suspend fun autoLogin(): Boolean {
            val loginCredentials = loginCredentialsDataStore.loginCredentials.firstOrNull()

            if (loginCredentials != null) {
                loginRepository
                    .login(loginCredentials)
                    .onSuccess { loginSession ->
                        _loginStatus.update { LoginStatus.Login(loginSession) }

                        return true
                    }.onFailure { throwable ->
                        ErrorEventBus.sendError(throwable.message)
                    }
            } else {
                _loginStatus.update { LoginStatus.Logout }
            }

            return false
        }

        suspend fun login(credentials: LoginCredentials): Boolean {
            if (loginStatus.value !is LoginStatus.Login) {
                loginRepository
                    .login(credentials)
                    .onSuccess { loginSession ->
                        _loginStatus.update { LoginStatus.Login(loginSession) }
                        loginCredentialsDataStore.saveLoginCredentials(credentials)

                        return true
                    }.onFailure { throwable ->
                        ErrorEventBus.sendError(throwable.message)
                    }
            }
            return false
        }

        suspend fun logout(): Boolean {
            val loginStatus = this@LoginManager.loginStatus.value

            if (loginStatus is LoginStatus.Login) {
                loginRepository
                    .logout(sessKey = loginStatus.loginSession.sessKey)
                    .onSuccess {
                        _loginStatus.update { LoginStatus.Logout }
                        loginCredentialsDataStore.deleteLoginCredentials()

                        return true
                    }.onFailure { throwable ->
                        ErrorEventBus.sendError(throwable.message)
                    }
            }

            return false
        }
    }
