package com.example.plato_calendar.domain.subject

import com.example.plato_calendar.domain.model.Schedule

interface CalendarRepository {
    fun getSchedules(): Result<List<Schedule>>
}