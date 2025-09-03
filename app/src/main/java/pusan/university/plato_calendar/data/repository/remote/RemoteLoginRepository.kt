package pusan.university.plato_calendar.data.repository.remote

import androidx.core.net.toUri
import pusan.university.plato_calendar.data.repository.remote.service.LoginService
import pusan.university.plato_calendar.domain.repository.LoginRepository
import retrofit2.HttpException
import java.net.CookieManager
import java.net.URI
import javax.inject.Inject

class RemoteLoginRepository @Inject constructor(
    private val loginService: LoginService,
    private val cookieManager: CookieManager
) : LoginRepository {
    override suspend fun login(userName: String, password: String): Result<String> {
        val response = loginService.login(
            userName = userName,
            password = password
        )

        val location = response.headers()["Location"]

        if (location != null) {
            val uri = location.toUri()

            if (uri.getQueryParameter("errorcode") == "3") {
                return Result.failure(IllegalStateException(INVALID_CREDENTIALS_ERROR))
            }

            val baseUrl = response.raw().request.url.newBuilder().encodedPath("/").build()
            val requestUri: URI = URI.create(baseUrl.toString())
            val cookies = cookieManager.cookieStore.get(requestUri)
            val moodleSession = cookies.firstOrNull { it.name == "MoodleSession" }?.value

            if (moodleSession != null) {
                return Result.success(moodleSession)
            }
        }

        return Result.failure(IllegalStateException(FAILED_LOGIN_ERROR))
    }

    override suspend fun logout(sessKey: String): Result<Unit> {
        val response = loginService.logout(sessKey = sessKey)
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(HttpException(response))
        }
    }

    companion object {
        private const val INVALID_CREDENTIALS_ERROR = "아이디 또는 패스워드가 잘못 입력되었습니다."
        private const val FAILED_LOGIN_ERROR = "로그인에 실패했습니다."
    }
}
