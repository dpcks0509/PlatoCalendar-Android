package com.example.plato_calendar.data.mapper

import com.example.plato_calendar.data.ScheduleDto
import com.example.plato_calendar.domain.model.Schedule

fun List<ScheduleDto>.toDomain(): List<Schedule> {
    return map(ScheduleDto::toDomain)
}

fun ScheduleDto.toDomain(): Schedule {
    return Schedule(
        id = this.id
    )
}