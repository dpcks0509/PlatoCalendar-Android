package pnu.plato.calendar.domain.repository

import pnu.plato.calendar.domain.entity.Schedule.AcademicSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule
import java.time.LocalDateTime

interface ScheduleRepository {
    suspend fun getAcademicSchedules(): Result<List<AcademicSchedule>>

    suspend fun getPersonalSchedules(sessKey: String): Result<List<PersonalSchedule>>

    suspend fun makePersonalSchedule(
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

    suspend fun deletePersonalSchedule(id: Long): Result<Unit>
}
