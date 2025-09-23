package pnu.plato.calendar.domain.repository

import pnu.plato.calendar.domain.entity.Schedule
import pnu.plato.calendar.domain.entity.Schedule.AcademicSchedule
import java.time.LocalDateTime

interface ScheduleRepository {
    suspend fun getAcademicSchedules(): Result<List<AcademicSchedule>>

    suspend fun getPersonalSchedules(sessKey: String): Result<List<Schedule.PersonalSchedule>>

    suspend fun makeCustomSchedule(
        title: String,
        description: String?,
        startAt: LocalDateTime,
        endAt: LocalDateTime,
    ): Result<Long>

    suspend fun editPersonalSchedule(
        id: Long,
        title: String,
        description: String?,
        startAt: LocalDateTime,
        endAt: LocalDateTime,
    ): Result<Unit>

    suspend fun deleteCustomSchedule(id: Long): Result<Unit>
}
