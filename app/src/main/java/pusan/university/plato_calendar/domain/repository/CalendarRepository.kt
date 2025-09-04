package pusan.university.plato_calendar.domain.repository

import pusan.university.plato_calendar.domain.entity.Schedule

interface CalendarRepository {
    fun getSchedules(sessKey: String): Result<List<Schedule>>
}