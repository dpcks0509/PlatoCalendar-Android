package pusan.university.plato_calendar.data.remote.repository

import pusan.university.plato_calendar.data.remote.service.CalendarService
import pusan.university.plato_calendar.domain.entity.Schedule
import pusan.university.plato_calendar.domain.repository.CalendarRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class RemoteCalendarRepository @Inject constructor(
    private val calendarService: CalendarService
) : CalendarRepository {
    override suspend fun getSchedules(sessKey: String): Result<List<Schedule>> {
        val response = calendarService.getSchedules(sessKey = sessKey)

        if (response.isSuccessful) {
            val body = response.body()?.string()
            if (body.isNullOrBlank()) {
                return Result.success(emptyList())
            }

            val schedules = parseIcsToSchedules(body)
            println(schedules)
            return Result.success(schedules)
        }

        return Result.failure(Exception("Failed"))
    }

    private fun parseIcsToSchedules(icsText: String): List<Schedule> {
        // Handle simple unfolded lines; for folded lines (starting with space), join with previous line
        val unfoldedLines = mutableListOf<String>()
        icsText.lines().forEach { rawLine ->
            if (rawLine.startsWith(" ") && unfoldedLines.isNotEmpty()) {
                val previous = unfoldedLines.removeAt(unfoldedLines.lastIndex)
                unfoldedLines.add(previous + rawLine.trimStart())
            } else {
                unfoldedLines.add(rawLine)
            }
        }

        val schedules = mutableListOf<Schedule>()
        var inEvent = false
        val currentFields = mutableMapOf<String, String>()

        fun buildScheduleFromFields(fields: Map<String, String>): Schedule {
            val uid = fields["UID"].orEmpty()
            val id = uid.substringBefore('@').toLongOrNull() ?: uid.hashCode().toLong()

            val summary = fields["SUMMARY"]
            val description = fields["DESCRIPTION"]
            val classification = fields["CLASS"]
            val lastModified = parseLocalDateTime(fields["LAST-MODIFIED"])
            val timestamp = parseLocalDateTime(fields["DTSTAMP"])
            val start = parseLocalDateTime(fields["DTSTART"])
            val end = parseLocalDateTime(fields["DTEND"])

            return Schedule(
                id = id,
                uid = uid,
                summary = summary,
                description = description,
                classification = classification,
                lastModified = lastModified,
                timestamp = timestamp,
                start = start,
                end = end
            )
        }

        unfoldedLines.forEach { line ->
            val trimmed = line.trim()
            when {
                trimmed.equals("BEGIN:VEVENT", ignoreCase = true) -> {
                    inEvent = true
                    currentFields.clear()
                }

                trimmed.equals("END:VEVENT", ignoreCase = true) -> {
                    if (inEvent) {
                        schedules.add(buildScheduleFromFields(currentFields.toMap()))
                    }
                    inEvent = false
                    currentFields.clear()
                }

                inEvent -> {
                    val colonIndex = trimmed.indexOf(':')
                    if (colonIndex > 0) {
                        val key = trimmed.substring(0, colonIndex).substringBefore(';').uppercase()
                        val value = trimmed.substring(colonIndex + 1)
                        currentFields[key] = value
                    }
                }
            }
        }

        return schedules
    }

    private fun parseLocalDateTime(value: String?): LocalDateTime? {
        if (value.isNullOrBlank()) return null
        // Try timestamp with offset (e.g., 20250904T070053Z)
        val offsetPattern = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX")
        try {
            val odt = OffsetDateTime.parse(value, offsetPattern)
            return odt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        } catch (_: Exception) {
        }

        // Fallback: date-only (e.g., 20250915)
        return try {
            val date = LocalDate.parse(value, DateTimeFormatter.BASIC_ISO_DATE)
            date.atStartOfDay()
        } catch (_: Exception) {
            null
        }
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
