package com.example.plato_calendar.data.repository.remote

import com.example.plato_calendar.data.mapper.toDomain
import com.example.plato_calendar.data.repository.remote.service.CalendarService
import com.example.plato_calendar.domain.model.Schedule
import com.example.plato_calendar.domain.repository.CalendarRepository
import javax.inject.Inject

class RemoteCalendarRepository @Inject constructor(
    private val calendarService: CalendarService
) : CalendarRepository {
    override fun getSchedules(): Result<List<Schedule>> {
        val sessKey = "" // todo get from local database

        return calendarService.getSchedules(sessKey = sessKey).map { it.toDomain() }
    }
}