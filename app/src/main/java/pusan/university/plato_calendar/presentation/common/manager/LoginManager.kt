package pusan.university.plato_calendar.presentation.common.manager

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pusan.university.plato_calendar.domain.entity.LoginStatus
import pusan.university.plato_calendar.domain.repository.LoginRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginManager @Inject constructor(
    private val loginRepository: LoginRepository,
    private val prefs: SharedPreferences
) {
    private val _loginStatus = MutableStateFlow<LoginStatus>(LoginStatus.Logout)
    val loginStatus: StateFlow<LoginStatus> = _loginStatus.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    suspend fun autoLogin(): Boolean {
        val userName = prefs.getString(USER_NAME, null) ?: "202055643"
        val password = prefs.getString(PASSWORD, null) ?: "mxkuy0508!"

        if (loginStatus.value is LoginStatus.Logout && userName != null && password != null) {
            loginRepository.login(userName = userName, password = password).onSuccess { loginSession ->
                _loginStatus.update { LoginStatus.Login(loginSession) }

                return true
            }.onFailure { throwable ->
                _errorMessage.update { throwable.message }
            }
        }
        return false
    }

    suspend fun login(userName: String, password: String): Boolean {
        if (loginStatus.value is LoginStatus.Logout) {
            loginRepository.login(userName = userName, password = password).onSuccess { loginSession ->
                _loginStatus.update { LoginStatus.Login(loginSession) }

                prefs.edit {
                    putString(USER_NAME, userName)
                    putString(PASSWORD, password)
                }

                return true
            }.onFailure { throwable ->
                _errorMessage.update { throwable.message }
            }
        }
        return false
    }

    suspend fun logout(): Boolean {
        val currentLoginStatus = loginStatus.value

        if (currentLoginStatus is LoginStatus.Login) {
            loginRepository.logout(sessKey = currentLoginStatus.loginSession.sessKey).onSuccess {
                _loginStatus.update { LoginStatus.Logout }

                prefs.edit { clear() }

                return true
            }.onFailure { throwable ->
                _errorMessage.update { throwable.message }
            }
        }

        return false
    }

    companion object {
        private const val USER_NAME = "username"
        private const val PASSWORD = "password"
    }
}