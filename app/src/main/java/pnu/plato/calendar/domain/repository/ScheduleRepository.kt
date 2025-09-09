package pnu.plato.calendar.domain.repository

import pnu.plato.calendar.domain.entity.AcademicSchedule
import pnu.plato.calendar.domain.entity.PersonalSchedule

interface ScheduleRepository {
    suspend fun getPersonalSchedules(sessKey: String): Result<List<PersonalSchedule>>

    suspend fun getAcademicSchedules(): Result<List<AcademicSchedule>>
}
