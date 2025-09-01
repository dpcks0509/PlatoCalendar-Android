package pnu.dpcks0509.plato_calendar.domain.repository

import pnu.dpcks0509.plato_calendar.domain.model.Schedule

interface CalendarRepository {
    fun getSchedules(): Result<List<Schedule>>
}