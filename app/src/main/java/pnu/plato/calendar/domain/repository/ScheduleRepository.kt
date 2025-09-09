package pnu.plato.calendar.domain.repository

import pnu.plato.calendar.domain.entity.AcademicSchedule
import pnu.plato.calendar.domain.entity.PersonalSchedule
import java.time.LocalDateTime

interface ScheduleRepository {
    suspend fun getAcademicSchedules(): Result<List<AcademicSchedule>>

    suspend fun getPersonalSchedules(sessKey: String): Result<List<PersonalSchedule>>

    suspend fun updatePersonalSchedule(
        id: Long,
        title: String,
        description: String?,
        startAt: LocalDateTime,
        endAt: LocalDateTime,
    ): Result<Unit>

    suspend fun deletePersonalSchedule(
        id: Long,
    ): Result<Unit>
}
