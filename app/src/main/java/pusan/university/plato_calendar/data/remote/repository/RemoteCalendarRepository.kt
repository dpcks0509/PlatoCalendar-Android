package pusan.university.plato_calendar.data.remote.repository

import pusan.university.plato_calendar.data.mapper.toDomain
import pusan.university.plato_calendar.data.remote.service.CalendarService
import pusan.university.plato_calendar.domain.entity.Schedule
import pusan.university.plato_calendar.domain.repository.CalendarRepository
import javax.inject.Inject

class RemoteCalendarRepository @Inject constructor(
    private val calendarService: CalendarService
) : CalendarRepository {
    override fun getSchedules(): Result<List<Schedule>> {
        val sessKey = "" // todo get from local database

        return calendarService.getSchedules(sessKey = sessKey).map { it.toDomain() }
    }
}