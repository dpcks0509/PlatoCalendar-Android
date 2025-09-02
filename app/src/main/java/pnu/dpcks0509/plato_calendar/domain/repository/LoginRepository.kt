package pnu.dpcks0509.plato_calendar.domain.repository

interface LoginRepository {
    suspend fun login(userName: String, password: String): Result<String>
    suspend fun logout(sessKey: String): Result<Unit>
}