package pusan.university.plato_calendar.domain.entity

sealed interface LoginStatus {
    data class Login(
        val loginSession: LoginSession,
    ) : LoginStatus

    data object Logout : LoginStatus

    data object Uninitialized : LoginStatus

    data object NetworkDisconnected : LoginStatus

    data object LoginInProgress : LoginStatus
}
