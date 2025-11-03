package pusan.university.plato_calendar.presentation.common.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import pusan.university.plato_calendar.app.network.NoNetworkConnectivityException
import pusan.university.plato_calendar.data.local.database.LoginCredentialsDataStore
import pusan.university.plato_calendar.domain.entity.LoginCredentials
import pusan.university.plato_calendar.domain.entity.LoginStatus
import pusan.university.plato_calendar.domain.repository.LoginRepository
import pusan.university.plato_calendar.presentation.common.eventbus.ToastEventBus
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
                    if (throwable is NoNetworkConnectivityException) {
                        _loginStatus.update { LoginStatus.NetworkDisconnected }
                    } else {
                        ToastEventBus.sendError(throwable.message)
                    }
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

                    ToastEventBus.sendSuccess("로그인에 성공했습니다.")
                    return true
                }.onFailure { throwable ->
                    ToastEventBus.sendError(throwable.message)
                }
        }
        return false
    }

    suspend fun logout(): Boolean {
        val loginStatus = loginStatus.value

        if (loginStatus is LoginStatus.Login) {
            loginRepository
                .logout(sessKey = loginStatus.loginSession.sessKey)
                .onSuccess {
                    _loginStatus.update { LoginStatus.Logout }
                    loginCredentialsDataStore.deleteLoginCredentials()

                    ToastEventBus.sendSuccess("로그아웃에 성공했습니다.")
                    return true
                }.onFailure { throwable ->
                    ToastEventBus.sendError(throwable.message)
                }
        }

        return false
    }
}
