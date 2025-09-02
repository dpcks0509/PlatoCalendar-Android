package pusan.university.plato_calendar.domain.repository

import pusan.university.plato_calendar.domain.model.Schedule

interface CalendarRepository {
    fun getSchedules(): Result<List<Schedule>>
}