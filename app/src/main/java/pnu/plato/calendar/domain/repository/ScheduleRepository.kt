package pnu.plato.calendar.domain.repository

import pnu.plato.calendar.domain.entity.Schedule.AcademicSchedule
import pnu.plato.calendar.domain.entity.Schedule.NewSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule

interface ScheduleRepository {
    suspend fun getAcademicSchedules(): Result<List<AcademicSchedule>>

    suspend fun getPersonalSchedules(sessKey: String): Result<List<PersonalSchedule>>

    suspend fun makeCustomSchedule(newSchedule: NewSchedule): Result<Long>

    suspend fun editPersonalSchedule(personalSchedule: PersonalSchedule): Result<Unit>

    suspend fun deleteCustomSchedule(id: Long): Result<Unit>
}
