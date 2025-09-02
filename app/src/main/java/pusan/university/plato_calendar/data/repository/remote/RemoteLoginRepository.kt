package pusan.university.plato_calendar.data.repository.remote

import pusan.university.plato_calendar.data.repository.remote.service.LoginService
import pusan.university.plato_calendar.domain.repository.LoginRepository
import pusan.university.plato_calendar.network.InMemoryCookieStore
import retrofit2.HttpException
import java.net.URLEncoder
import javax.inject.Inject

class RemoteLoginRepository @Inject constructor(
    private val loginService: LoginService,
    private val cookieStore: InMemoryCookieStore
) : LoginRepository {
    override suspend fun login(userName: String, password: String): Result<String> {
        val encodedUserName = URLEncoder.encode(userName, Charsets.UTF_8.name())
        val encodedPassword = URLEncoder.encode(password, Charsets.UTF_8.name())
        val requestBody = "username=$encodedUserName&password=$encodedPassword"
        val contentLength = requestBody.length.toString()

        println("Request Body: $requestBody")
        println("Content Length: $contentLength")

        val response = loginService.login(
            contentLength = contentLength,
            userName = userName,
            password = password
        )
        
        if (response.code() == 303) {
            val baseUrl = response.raw().request.url.newBuilder().encodedPath("/").build()
            val moodleSession = cookieStore.getCookieValue(baseUrl.host, "MoodleSession")

            moodleSession?.let { return Result.success(moodleSession) }
        } else {
            val errorCode = response.raw().request.url.queryParameter("errorcode")

            if (errorCode == "3") {
                return Result.failure(IllegalStateException(INVALID_CREDENTIALS_ERROR))
            }
        }

        return Result.failure(IllegalStateException(UNKNOWN_LOGIN_ERROR))
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
        private const val UNKNOWN_LOGIN_ERROR = "알 수 없는 로그인 오류가 발생했습니다."
    }
}
