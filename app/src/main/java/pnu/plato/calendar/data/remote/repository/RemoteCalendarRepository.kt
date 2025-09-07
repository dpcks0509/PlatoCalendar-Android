package pnu.plato.calendar.data.remote.repository

import pnu.plato.calendar.data.remote.service.CalendarService
import pnu.plato.calendar.domain.entity.Schedule
import pnu.plato.calendar.domain.repository.CalendarRepository
import pnu.plato.calendar.presentation.common.function.parseIcsToSchedules
import javax.inject.Inject

class RemoteCalendarRepository @Inject constructor(
    private val calendarService: CalendarService
) : CalendarRepository {
    override suspend fun getSchedules(sessKey: String): Result<List<Schedule>> {
        val response = calendarService.getSchedules(sessKey = sessKey)

        if (response.isSuccessful) {
            val body = response.body()?.string()
            if (body.isNullOrBlank()) {
                return Result.success(emptyList())
            }

            val schedules = body.parseIcsToSchedules()
            return Result.success(schedules)
        }

        return Result.failure(Exception(GET_SCHEDULES_FAILED_ERROR))
    }

    companion object {
        private const val GET_SCHEDULES_FAILED_ERROR = "일정을 가져오는데 실패했습니다."
    }
}
