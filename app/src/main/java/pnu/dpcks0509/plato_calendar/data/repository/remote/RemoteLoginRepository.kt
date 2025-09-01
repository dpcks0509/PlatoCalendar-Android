package pnu.dpcks0509.plato_calendar.data.repository.remote

import pnu.dpcks0509.plato_calendar.data.repository.remote.service.LoginService
import pnu.dpcks0509.plato_calendar.domain.repository.LoginRepository
import javax.inject.Inject

class RemoteLoginRepository @Inject constructor(
    private val loginService: LoginService
) : LoginRepository {
    override fun login(username: String, password: String): Result<Unit> {
        return loginService.login(username = username, password = password)
    }

    override fun logout(sessKey: String): Result<Unit> {
        return loginService.logout(sessKey = sessKey)
    }
}
