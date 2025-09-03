package pusan.university.plato_calendar.data.mapper

import pusan.university.plato_calendar.data.dto.ScheduleDto
import pusan.university.plato_calendar.domain.entity.Schedule

fun List<ScheduleDto>.toDomain(): List<Schedule> {
    return map(ScheduleDto::toDomain)
}

fun ScheduleDto.toDomain(): Schedule {
    return Schedule(
        id = this.id
    )
}