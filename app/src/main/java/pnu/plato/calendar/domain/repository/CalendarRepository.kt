package pnu.plato.calendar.domain.repository

import pnu.plato.calendar.domain.entity.Schedule

interface CalendarRepository {
    suspend fun getSchedules(sessKey: String): Result<List<Schedule>>
}