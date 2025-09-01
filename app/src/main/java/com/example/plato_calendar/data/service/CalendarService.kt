package com.example.plato_calendar.data.service

import com.example.plato_calendar.BuildConfig
import com.example.plato_calendar.data.ScheduleDto
import retrofit2.http.POST

private const val BASE_URL = BuildConfig.PLATO_BASE_URL + "calendar/"

interface CalendarService {
    @POST(BASE_URL + "export.php")
    fun getSchedules(sessKey: String): Result<List<ScheduleDto>>
}