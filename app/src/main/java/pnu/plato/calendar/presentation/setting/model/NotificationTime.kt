package pnu.plato.calendar.presentation.setting.model

enum class NotificationTime(
    val label: String,
) {
    NONE("없음"),
    ONE_HOUR("1시간 전"),
    TWO_HOURS("2시간 전"),
    SIX_HOURS("6시간 전"),
    TWELVE_HOURS("12시간 전"),
    ONE_DAY("1일 전"),
    TWO_DAYS("2일 전"),
    ONE_WEEK("1주 전"),
}
