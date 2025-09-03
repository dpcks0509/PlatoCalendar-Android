package pusan.university.plato_calendar.data.repository.remote

import pusan.university.plato_calendar.data.repository.remote.service.LoginService
import pusan.university.plato_calendar.domain.model.LoginInfo
import pusan.university.plato_calendar.domain.repository.LoginRepository
import java.net.CookieManager
import java.net.URI
import javax.inject.Inject

class RemoteLoginRepository @Inject constructor(
    private val loginService: LoginService,
    private val cookieManager: CookieManager
) : LoginRepository {
    override suspend fun login(userName: String, password: String): Result<LoginInfo> {
        val response = loginService.login(
            userName = userName,
            password = password
        )

        if (response.isSuccessful) {
            val requestUrl = response.raw().request.url

            if (requestUrl.encodedPath == "/login.php" && requestUrl.queryParameter("errorcode") == "3") {
                return Result.failure(IllegalStateException(INVALID_CREDENTIALS_ERROR))
            }

            val baseUrl = requestUrl.newBuilder().encodedPath("/").query(null).build()
            val requestUri: URI = URI.create(baseUrl.toString())
            val cookies = cookieManager.cookieStore.get(requestUri)
            val moodleSession = cookies.firstOrNull { it.name == "MoodleSession" }?.value

            val bodyString = response.body()?.string().orEmpty()
            val sessKey = Regex(
                pattern = """M\.cfg\s*=\s*\{[\s\S]*?"sesskey"\s*:\s*"([^"]+)"""
            ).find(bodyString)?.groupValues?.getOrNull(1)

            if (moodleSession != null && !sessKey.isNullOrBlank()) {
                return Result.success(LoginInfo(moodleSession = moodleSession, sessKey = sessKey))
            }
        }

        return Result.failure(IllegalStateException(LOGIN_FAILED_ERROR))
    }

    override suspend fun logout(sessKey: String): Result<Unit> {
        val response = loginService.logout(sessKey = sessKey)

        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalStateException(LOGOUT_FAILED_ERROR))
        }
    }

    companion object {
        private const val INVALID_CREDENTIALS_ERROR = "아이디 또는 패스워드가 잘못 입력되었습니다."
        private const val LOGIN_FAILED_ERROR = "로그인에 실패했습니다."
        private const val LOGOUT_FAILED_ERROR = "로그아웃에 실패했습니다."
    }
}
