package pnu.plato.calendar.domain.repository

import pnu.plato.calendar.domain.entity.LoginCredentials
import pnu.plato.calendar.domain.entity.LoginSession

interface LoginRepository {
    suspend fun login(credentials: LoginCredentials): Result<LoginSession>
    suspend fun logout(sessKey: String): Result<Unit>
}