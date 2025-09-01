package pnu.dpcks0509.plato_calendar.data.repository.remote.service

import pnu.dpcks0509.plato_calendar.data.dto.ScheduleDto
import retrofit2.http.POST

private const val CALENDAR_BASE_URL = "calendar/"

interface CalendarService {
    @POST(CALENDAR_BASE_URL + "export.php")
    fun getSchedules(sessKey: String): Result<List<ScheduleDto>>
}