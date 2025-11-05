package pusan.university.plato_calendar.domain.repository

import pusan.university.plato_calendar.domain.entity.Schedule.AcademicSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.NewSchedule
import pusan.university.plato_calendar.domain.entity.Schedule.PersonalSchedule

interface ScheduleRepository {
    suspend fun getAcademicSchedules(): Result<List<AcademicSchedule>>

    suspend fun getPersonalSchedules(sessKey: String): Result<List<PersonalSchedule>>

    suspend fun makeCustomSchedule(newSchedule: NewSchedule): Result<Long>

    suspend fun editPersonalSchedule(personalSchedule: PersonalSchedule): Result<Unit>

    suspend fun deleteCustomSchedule(id: Long): Result<Unit>

    suspend fun markScheduleAsCompleted(id: Long)

    suspend fun markScheduleAsUncompleted(id: Long)
}
