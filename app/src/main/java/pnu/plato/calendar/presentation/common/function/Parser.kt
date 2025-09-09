package pnu.plato.calendar.presentation.common.function

import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule
import java.time.LocalDateTime

fun String.parseUctToLocalDateTime(): LocalDateTime {
    val year = substring(0, 4).toInt()
    val month = substring(4, 6).toInt()
    val day = substring(6, 8).toInt()
    val hour = substring(9, 11).toInt()
    val minute = substring(11, 13).toInt()

    return LocalDateTime.of(year, month, day, hour, minute)
}

fun String.parseIcsToPersonalSchedules(): List<PersonalSchedule> {
    val unfoldedLines = mutableListOf<String>()
    lines().forEach { rawLine ->
        if (rawLine.startsWith(" ") && unfoldedLines.isNotEmpty()) {
            val previous = unfoldedLines.removeAt(unfoldedLines.lastIndex)
            unfoldedLines.add(previous + rawLine.trimStart())
        } else {
            unfoldedLines.add(rawLine)
        }
    }

    val personalSchedules = mutableListOf<PersonalSchedule>()
    var inEvent = false
    val currentFields = mutableMapOf<String, String>()

    fun buildScheduleFromFields(fields: Map<String, String>): PersonalSchedule =
        PersonalSchedule(
            id = fields["UID"].orEmpty().split("@")[0].toLong(),
            title = fields["SUMMARY"].orEmpty(),
            description = fields["DESCRIPTION"],
            startAt = fields["DTSTART"].orEmpty().parseUctToLocalDateTime(),
            endAt = fields["DTEND"].orEmpty().parseUctToLocalDateTime(),
            courseCode = fields["CATEGORIES"]?.split("_")[2],
        )

    unfoldedLines.forEach { line ->
        val trimmed = line.trim()
        when {
            trimmed.equals("BEGIN:VEVENT", ignoreCase = true) -> {
                inEvent = true
                currentFields.clear()
            }

            trimmed.equals("END:VEVENT", ignoreCase = true) -> {
                if (inEvent) {
                    personalSchedules.add(buildScheduleFromFields(currentFields.toMap()))
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

    return personalSchedules
}
