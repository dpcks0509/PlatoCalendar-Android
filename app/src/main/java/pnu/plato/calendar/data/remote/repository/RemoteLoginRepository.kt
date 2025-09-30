package pnu.plato.calendar.data.remote.repository

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import pnu.plato.calendar.data.remote.service.LoginService
import pnu.plato.calendar.domain.entity.LoginCredentials
import pnu.plato.calendar.domain.entity.LoginSession
import pnu.plato.calendar.domain.repository.LoginRepository
import javax.inject.Inject

class RemoteLoginRepository
    @Inject
    constructor(
        private val loginService: LoginService,
    ) : LoginRepository {
        override suspend fun login(credentials: LoginCredentials): Result<LoginSession> {
            val response =
                loginService.login(
                    userName = credentials.userName,
                    password = credentials.password,
                )

            if (response.code() == REDIRECT_CODE) {
                val redirectLocation =
                    response.headers()["Location"]
                        ?: return Result.failure(Exception(LOGIN_FAILED_ERROR))
                val redirectUrl = redirectLocation.toHttpUrlOrNull()

                when (redirectUrl?.queryParameter("errorcode")) {
                    "3" -> return Result.failure(Exception(INVALID_CREDENTIALS_ERROR))
                    "4" -> return Result.failure(Exception(SESSION_EXPIRED_ERROR))
                }

                val userId =
                    redirectUrl?.queryParameter("testsession")
                        ?: return Result.failure(Exception(LOGIN_FAILED_ERROR))

                val redirectResponse = loginService.loginRedirect()

                if (redirectResponse.isSuccessful) {
                    val redirectResponseBody = redirectResponse.body()?.string() ?: return Result.failure(Exception(LOGIN_FAILED_ERROR))

                    val sessKey =
                        Regex(
                            pattern = """M\.cfg\s*=\s*\{[\s\S]*?"sesskey"\s*:\s*"([^"]+)""",
                        ).find(redirectResponseBody)?.groupValues?.getOrNull(1)
                            ?: return Result.failure(Exception(LOGIN_FAILED_ERROR))

                    val fullName =
                        Regex(
                            pattern = """class="fullname"[^>]*title="([^"]+)"""",
                        ).find(redirectResponseBody)?.groupValues?.getOrNull(1) ?: return Result.failure(Exception(LOGIN_FAILED_ERROR))

                    return Result.success(
                        LoginSession(
                            userName = credentials.userName,
                            fullName = fullName,
                            userId = userId,
                            sessKey = sessKey,
                        ),
                    )
                }
            }

            return Result.failure(Exception(LOGIN_FAILED_ERROR))
        }

        override suspend fun logout(sessKey: String): Result<Unit> {
            val response = loginService.logout(sessKey = sessKey)

            return if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(LOGOUT_FAILED_ERROR))
            }
        }

        companion object {
            private const val REDIRECT_CODE = 303
            private const val INVALID_CREDENTIALS_ERROR = "아이디 또는 패스워드가 잘못 입력되었습니다."
            private const val SESSION_EXPIRED_ERROR = "세션이 종료 되었습니다. 다시 로그인 하십시오."
            private const val LOGIN_FAILED_ERROR = "로그인에 실패했습니다."
            private const val LOGOUT_FAILED_ERROR = "로그아웃에 실패했습니다."
        }
    }
