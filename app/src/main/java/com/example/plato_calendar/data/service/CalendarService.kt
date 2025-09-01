package com.example.plato_calendar.data.service

import com.example.plato_calendar.data.ScheduleDto
import retrofit2.http.POST

private const val CALENDAR_BASE_URL = "calendar/"

interface CalendarService {
    @POST(CALENDAR_BASE_URL + "export.php")
    fun getSchedules(sessKey: String): Result<List<ScheduleDto>>
}