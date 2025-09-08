package pnu.plato.calendar.domain.repository

import pnu.plato.calendar.domain.entity.Schedule.AcademicSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule

interface ScheduleRepository {
    suspend fun getPersonalSchedules(sessKey: String): Result<List<PersonalSchedule>>

    suspend fun getAcademicSchedules(): Result<List<AcademicSchedule>>
}
