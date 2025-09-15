package pnu.plato.calendar.domain.entity

enum class DayOfWeek {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;

    val isWeekend: Boolean
        get() = this == SATURDAY || this == SUNDAY
}