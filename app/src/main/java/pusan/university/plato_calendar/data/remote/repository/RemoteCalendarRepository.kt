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
            val body = response.body()?.string()
            if (body.isNullOrBlank()) {
                return Result.success(emptyList())
            }

            val schedules = parseIcsToSchedules(body)
            return Result.success(schedules)
        }

        return Result.failure(Exception(GET_SCHEDULES_FAILED_ERROR))
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
            return Schedule(
                uid = fields["UID"].orEmpty(),
                summary = fields["SUMMARY"],
                description = fields["DESCRIPTION"],
                classification = fields["CLASS"],
                lastModified = fields["LAST-MODIFIED"],
                timestamp = fields["DTSTAMP"],
                start = fields["DTSTART"],
                end = fields["DTEND"],
                categories = fields["CATEGORIES"]
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

    companion object {
        private const val GET_SCHEDULES_FAILED_ERROR = "일정을 가져오는데 실패했습니다."
    }
}
