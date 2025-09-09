package pnu.plato.calendar.presentation.common.function

import pnu.plato.calendar.domain.entity.AcademicSchedule
import pnu.plato.calendar.domain.entity.PersonalSchedule
import java.time.LocalDateTime
import java.time.LocalTime


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

private fun buildScheduleFromFields(fields: Map<String, String>): PersonalSchedule {
    return PersonalSchedule(
        id = fields["UID"].orEmpty().split("@")[0].toLong(),
        title = fields["SUMMARY"].orEmpty(),
        description = fields["DESCRIPTION"],
        startAt = fields["DTSTART"].orEmpty().parseUctToLocalDateTime(),
        endAt = fields["DTEND"].orEmpty().parseUctToLocalDateTime(),
        courseCode = fields["CATEGORIES"]?.split("_")[2],
    )
}

private fun String.parseUctToLocalDateTime(): LocalDateTime {
    val year = substring(0, 4).toInt()
    val month = substring(4, 6).toInt()
    val day = substring(6, 8).toInt()
    val hour = substring(9, 11).toInt()
    val minute = substring(11, 13).toInt()

    return LocalDateTime.of(year, month, day, hour, minute)
}

fun String.parseHtmlToAcademicSchedules(): List<AcademicSchedule> {
    val academicSchedules = mutableListOf<AcademicSchedule>()

    val tableRows = this.split("<tr>").drop(1)

    tableRows.forEach { row ->
        if (row.contains("class=\"term\"") && row.contains("class=\"text\"")) {
            try {
                val termMatch = Regex("class=\"term\"[^>]*>([^<]+)</").find(row)
                val termText = termMatch?.groupValues?.get(1)?.trim()

                val textMatch = Regex("class=\"text\"[^>]*>([^<]+)</").find(row)
                val textContent = textMatch?.groupValues?.get(1)?.trim()

                if (termText != null && textContent != null) {
                    val (startAt, endAt) = termText.parseDateRange()

                    academicSchedules.add(
                        AcademicSchedule(
                            title = textContent,
                            startAt = startAt,
                            endAt = endAt
                        )
                    )
                }
            } catch (e: Exception) {
                return@forEach
            }
        }
    }

    return academicSchedules
}

private fun String.parseDateRange(): Pair<LocalTime, LocalTime> {
    val dates = this.split(" - ").map { it.trim() }

    if (dates.size != 2) {
        throw IllegalArgumentException("Invalid date range format: $this")
    }

    val startDate = dates[0].parseKoreanDateToLocalTime()
    val endDate = dates[1].parseKoreanDateToLocalTime()

    return Pair(startDate, endDate)
}

private fun String.parseKoreanDateToLocalTime(): LocalTime {
    val parts = this.split(".")

    if (parts.size != 3) {
        throw IllegalArgumentException("Invalid date format: $this")
    }

    val year = parts[0].toInt()
    val month = parts[1].toInt()
    val day = parts[2].toInt()

    return LocalTime.of(year, month, day)
}
