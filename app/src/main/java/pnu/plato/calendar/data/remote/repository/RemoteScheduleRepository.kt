package pnu.plato.calendar.data.remote.repository

import androidx.core.net.toUri
import pnu.plato.calendar.data.remote.service.PlatoService
import pnu.plato.calendar.data.remote.service.PnuService
import pnu.plato.calendar.domain.entity.AcademicSchedule
import pnu.plato.calendar.domain.entity.PersonalSchedule
import pnu.plato.calendar.domain.repository.ScheduleRepository
import pnu.plato.calendar.presentation.common.function.parseHtmlToAcademicSchedules
import pnu.plato.calendar.presentation.common.function.parseIcsToPersonalSchedules
import pnu.plato.calendar.presentation.common.manager.LoginManager
import java.time.LocalDateTime
import javax.inject.Inject

class RemoteScheduleRepository
    @Inject
    constructor(
        private val platoService: PlatoService,
        private val pnuService: PnuService,
        private val loginManager: LoginManager,
    ) : ScheduleRepository {
        override suspend fun getAcademicSchedules(): Result<List<AcademicSchedule>> {
            val response = pnuService.getAcademicSchedules()

            if (response.isSuccessful) {
                val body = response.body()?.string()
                if (body.isNullOrBlank()) {
                    return Result.success(emptyList())
                }

                val academicSchedules = body.parseHtmlToAcademicSchedules()
                return Result.success(academicSchedules)
            }

            return Result.failure(Exception(GET_SCHEDULES_FAILED_ERROR))
        }

        override suspend fun getPersonalSchedules(sessKey: String): Result<List<PersonalSchedule>> {
            val response = platoService.getPersonalSchedules(sessKey = sessKey)

            if (response.isSuccessful) {
                val body = response.body()?.string()
                if (body.isNullOrBlank()) {
                    return Result.success(emptyList())
                }

                val exportUrl =
                    response
                        .raw()
                        .request.url
                        .toString()
                val userId = exportUrl.toUri().getQueryParameter("userid")
                loginManager.setUserId(userId)

                val personalSchedules = body.parseIcsToPersonalSchedules()
                return Result.success(personalSchedules)
            }

            return Result.failure(Exception(GET_SCHEDULES_FAILED_ERROR))
        }

        override suspend fun updatePersonalSchedule(
            id: Long,
            title: String,
            description: String?,
            startAt: LocalDateTime,
            endAt: LocalDateTime,
        ): Result<Unit> {
            TODO("Not yet implemented")
        }

        override suspend fun deletePersonalSchedule(id: Long): Result<Unit> {
            TODO("Not yet implemented")
        }

        companion object {
            private const val GET_SCHEDULES_FAILED_ERROR = "일정을 가져오는데 실패했습니다."
        }
    }
