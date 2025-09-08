package pnu.plato.calendar.domain.repository

import pnu.plato.calendar.domain.entity.Schedule.StudentSchedule

interface CalendarRepository {
    suspend fun getStudentSchedules(sessKey: String): Result<List<StudentSchedule>>
}