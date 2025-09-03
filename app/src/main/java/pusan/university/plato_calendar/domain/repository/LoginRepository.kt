package pusan.university.plato_calendar.domain.repository

import pusan.university.plato_calendar.domain.entity.LoginInfo

interface LoginRepository {
    suspend fun login(userName: String, password: String): Result<LoginInfo>
    suspend fun logout(sessKey: String): Result<Unit>
}