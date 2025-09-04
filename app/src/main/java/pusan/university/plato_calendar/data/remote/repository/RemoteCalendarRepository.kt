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
            val summary = fields["SUMMARY"]
            val description = fields["DESCRIPTION"]
            val classification = fields["CLASS"]
            val lastModified = parseLocalDateTime(fields["LAST-MODIFIED"])
            val timestamp = parseLocalDateTime(fields["DTSTAMP"])
            val start = parseLocalDateTime(fields["DTSTART"])
            val end = parseLocalDateTime(fields["DTEND"])
            val categories = fields["CATEGORIES"]

            return Schedule(
                uid = uid,
                summary = summary,
                description = description,
                classification = classification,
                lastModified = lastModified,
                timestamp = timestamp,
                start = start,
                end = end,
                categories = categories
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

        val offsetPattern = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX")

        try {
            val odt = OffsetDateTime.parse(value, offsetPattern)
            return odt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        } catch (_: Exception) {
        }

        return try {
            val date = LocalDate.parse(value, DateTimeFormatter.BASIC_ISO_DATE)
            date.atStartOfDay()
        } catch (_: Exception) {
            null
        }
    }
}
