package pusan.university.plato_calendar.domain.entity

data class LoginSession(
    val userName: String,
    val fullName: String,
    val userId: String,
    val sessKey: String,
) {
    val userInfo: String get() = "$fullName ($userName)"
}
