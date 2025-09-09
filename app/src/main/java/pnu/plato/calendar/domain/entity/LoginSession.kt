package pnu.plato.calendar.domain.entity

data class LoginSession(
    val moodleSession: String,
    val sessKey: String,
    val userId: String? = null
)
