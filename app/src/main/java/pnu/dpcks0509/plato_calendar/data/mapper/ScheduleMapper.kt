package pnu.dpcks0509.plato_calendar.data.mapper

import pnu.dpcks0509.plato_calendar.data.dto.ScheduleDto
import pnu.dpcks0509.plato_calendar.domain.model.Schedule

fun List<ScheduleDto>.toDomain(): List<Schedule> {
    return map(ScheduleDto::toDomain)
}

fun ScheduleDto.toDomain(): Schedule {
    return Schedule(
        id = this.id
    )
}