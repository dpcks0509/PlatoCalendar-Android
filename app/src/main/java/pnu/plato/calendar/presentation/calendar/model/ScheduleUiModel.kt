package pnu.plato.calendar.presentation.calendar.model

import androidx.compose.ui.graphics.Color
import pnu.plato.calendar.domain.entity.Schedule
import pnu.plato.calendar.presentation.common.theme.CalendarGraphite
import pnu.plato.calendar.presentation.common.theme.CalendarLavender
import pnu.plato.calendar.presentation.common.theme.CalendarSage
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

sealed class ScheduleUiModel {
    abstract val title: String
    abstract val color: Color

    data class AcademicScheduleUiModel(
        override val title: String,
        val startAt: LocalDate,
        val endAt: LocalDate,
    ) : ScheduleUiModel() {
        override val color: Color = CalendarLavender

        constructor(domain: Schedule.AcademicSchedule) : this(
            title = domain.title,
            startAt = domain.startAt,
            endAt = domain.endAt,
        )
    }

    data class PersonalScheduleUiModel(
        val id: Long,
        override val title: String,
        val description: String?,
        val startAt: LocalDateTime,
        val endAt: LocalDateTime,
        val courseName: String?,
    ) : ScheduleUiModel() {
        constructor(domain: Schedule.PersonalSchedule, courseName: String?) : this(
            id = domain.id,
            title =
                if (startsWithComplete(domain.title)) {
                    domain.title.removePrefix(COMPLETE)
                } else {
                    domain.title
                },
            description = domain.description,
            startAt = domain.startAt,
            endAt = domain.endAt,
            courseName = courseName,
        )

        val isComplete: Boolean get() = startsWithComplete(title)

        val deadLine: String
            get() {
                val formatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)
                return endAt.format(formatter) + " 까지"
            }

        override val color: Color get() = if (!isComplete) CalendarSage else CalendarGraphite

        companion object {
            const val COMPLETE = "(완료) "

            private fun startsWithComplete(title: String): Boolean = title.startsWith(COMPLETE)
        }
    }
}
