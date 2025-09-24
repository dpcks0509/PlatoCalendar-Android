package pnu.plato.calendar.presentation.calendar.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import pnu.plato.calendar.domain.entity.Schedule.AcademicSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pnu.plato.calendar.presentation.common.theme.CalendarFlamingo
import pnu.plato.calendar.presentation.common.theme.CalendarGraphite
import pnu.plato.calendar.presentation.common.theme.CalendarLavender
import pnu.plato.calendar.presentation.common.theme.CalendarSage
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

sealed class ScheduleUiModel {
    abstract val title: String
    abstract val color: Color @Composable get

    data class AcademicScheduleUiModel(
        override val title: String,
        val startAt: LocalDate,
        val endAt: LocalDate,
    ) : ScheduleUiModel() {
        override val color: Color @Composable get() = CalendarLavender

        constructor(domain: AcademicSchedule) : this(
            title = domain.title,
            startAt = domain.startAt,
            endAt = domain.endAt,
        )
    }

    sealed class PersonalScheduleUiModel : ScheduleUiModel() {
        abstract val id: Long
        abstract val description: String?
        abstract val startAt: LocalDateTime
        abstract val endAt: LocalDateTime
        abstract override val title: String

        val isComplete: Boolean get() = title.startsWith(COMPLETE)

        val deadLine: String
            get() = endAt.format(TIME_FORMATTER) + " 까지"

        data class CourseScheduleUiModel(
            override val id: Long,
            override val title: String,
            override val description: String?,
            override val startAt: LocalDateTime,
            override val endAt: LocalDateTime,
            val courseName: String,
        ) : PersonalScheduleUiModel() {
            constructor(domain: PersonalSchedule.CourseSchedule, courseName: String) : this(
                id = domain.id,
                title = domain.title.removePrefix(COMPLETE),
                description = domain.description,
                startAt = domain.startAt,
                endAt = domain.endAt,
                courseName = courseName,
            )

            override val color: Color
                @Composable get() =
                    if (!isComplete) CalendarSage else CalendarGraphite
        }

        data class CustomScheduleUiModel(
            override val id: Long,
            override val title: String,
            override val description: String?,
            override val startAt: LocalDateTime,
            override val endAt: LocalDateTime,
        ) : PersonalScheduleUiModel() {
            constructor(domain: CustomSchedule) : this(
                id = domain.id,
                title = domain.title.removePrefix(COMPLETE),
                description = domain.description,
                startAt = domain.startAt,
                endAt = domain.endAt,
            )

            override val color: Color
                @Composable get() =
                    if (!isComplete) CalendarFlamingo else CalendarGraphite
        }

        companion object {
            const val COMPLETE = "(완료) "
            private val TIME_FORMATTER =
                DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)
        }
    }
}
