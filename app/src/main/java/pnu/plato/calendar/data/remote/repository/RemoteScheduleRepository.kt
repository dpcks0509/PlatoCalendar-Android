package pnu.plato.calendar.data.remote.repository

import pnu.plato.calendar.data.remote.service.PlatoService
import pnu.plato.calendar.data.remote.service.PnuService
import pnu.plato.calendar.domain.entity.Schedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule
import pnu.plato.calendar.domain.repository.ScheduleRepository
import pnu.plato.calendar.presentation.common.function.parseIcsToPersonalSchedules
import javax.inject.Inject

class RemoteScheduleRepository
    @Inject
    constructor(
        private val platoService: PlatoService,
        private val pnuService: PnuService,
    ) : ScheduleRepository {
        override suspend fun getPersonalSchedules(sessKey: String): Result<List<PersonalSchedule>> {
            val response = platoService.getPersonalSchedules(sessKey = sessKey)

            if (response.isSuccessful) {
                val body = response.body()?.string()
                if (body.isNullOrBlank()) {
                    return Result.success(emptyList())
                }

                val schedules = body.parseIcsToPersonalSchedules()
                return Result.success(schedules)
            }

            return Result.failure(Exception(GET_SCHEDULES_FAILED_ERROR))
        }

        override suspend fun getAcademicSchedules(): Result<List<Schedule.AcademicSchedule>> {
            val response = pnuService.getAcademicSchedules()

            if (response.isSuccessful) {
                val body = response.body()?.string().apply { println(this) }
                if (body.isNullOrBlank()) {
                    return Result.success(emptyList())
                }

//                val schedules = body.parseIcsToAcademicSchedules()
//                return Result.success(schedules)
            }

            return Result.failure(Exception(GET_SCHEDULES_FAILED_ERROR))
        }

        companion object {
            private const val GET_SCHEDULES_FAILED_ERROR = "일정을 가져오는데 실패했습니다."
        }
    }
