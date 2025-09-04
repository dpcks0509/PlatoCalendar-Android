package pusan.university.plato_calendar.data.remote.repository

import pusan.university.plato_calendar.data.remote.service.CalendarService
import pusan.university.plato_calendar.domain.entity.Schedule
import pusan.university.plato_calendar.domain.repository.CalendarRepository
import javax.inject.Inject

class RemoteCalendarRepository @Inject constructor(
    private val calendarService: CalendarService
) : CalendarRepository {
    override suspend fun getSchedules(sessKey: String): Result<List<Schedule>> {
        val response = calendarService.getSchedules(sessKey = sessKey)

        if (response.isSuccessful) {
            return Result.success(emptyList())
        }

        return Result.failure(Exception("Failed"))
    }
}

//BEGIN:VCALENDAR
//METHOD:PUBLISH
//PRODID:-//Moodle Pty Ltd//NONSGML Moodle Version 2018051709//EN
//VERSION:2.0
//
//
//BEGIN:VEVENT
//UID:5237424@plato.pusan.ac.kr
//SUMMARY:915
//DESCRIPTION:
//CLASS:PUBLIC
//LAST-MODIFIED:20250901T081744Z
//DTSTAMP:20250904T044902Z
//DTSTART:20250915T081700Z
//DTEND:20250915T081700Z
//END:VEVENT
//
//
//BEGIN:VEVENT
//UID:5237426@plato.pusan.ac.kr
//SUMMARY:1015
//DESCRIPTION:
//CLASS:PUBLIC
//LAST-MODIFIED:20250901T081756Z
//DTSTAMP:20250904T044902Z
//DTSTART:20251010T081700Z
//DTEND:20251010T081700Z
//END:VEVENT
//
//
//END:VCALENDAR
