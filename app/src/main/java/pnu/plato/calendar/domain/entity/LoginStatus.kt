package pnu.plato.calendar.domain.entity

sealed interface LoginStatus {
    data class Login(val loginSession: LoginSession) : LoginStatus
    data object Logout : LoginStatus
    data object Uninitialized : LoginStatus
}