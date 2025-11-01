package pnu.plato.calendar.presentation.calendar.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import pnu.plato.calendar.domain.entity.Schedule.AcademicSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pnu.plato.calendar.presentation.common.extension.formatTimeWithMidnightSpecialCase
import pnu.plato.calendar.presentation.common.theme.CalendarFlamingo
import pnu.plato.calendar.presentation.common.theme.CalendarLavender
import pnu.plato.calendar.presentation.common.theme.CalendarSage
import pnu.plato.calendar.presentation.common.theme.MediumGray
import java.time.LocalDate
import java.time.LocalDateTime

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
        abstract override val title: String
        abstract val description: String?
        abstract val startAt: LocalDateTime
        abstract val endAt: LocalDateTime
        abstract val isCompleted: Boolean

        val deadLine: String
            get() = endAt.formatTimeWithMidnightSpecialCase() + " 까지"

        data class CourseScheduleUiModel(
            override val id: Long,
            override val title: String,
            override val description: String?,
            override val startAt: LocalDateTime,
            override val endAt: LocalDateTime,
            override val isCompleted: Boolean,
            val courseName: String,
        ) : PersonalScheduleUiModel() {
            val titleWithCourseName: String get() = if (courseName.isEmpty()) title else "${courseName}_$title"

            constructor(domain: PersonalSchedule.CourseSchedule, courseName: String) : this(
                id = domain.id,
                title = domain.title.removePrefix(COMPLETE),
                description = domain.description,
                startAt = domain.startAt,
                endAt = domain.endAt,
                isCompleted = domain.isCompleted,
                courseName = courseName,
            )

            override val color: Color
                @Composable get() =
                    if (!isCompleted) CalendarSage else MediumGray
        }

        data class CustomScheduleUiModel(
            override val id: Long,
            override val title: String,
            override val description: String?,
            override val startAt: LocalDateTime,
            override val endAt: LocalDateTime,
            override val isCompleted: Boolean,
        ) : PersonalScheduleUiModel() {
            constructor(domain: CustomSchedule) : this(
                id = domain.id,
                title = domain.title.removePrefix(COMPLETE),
                description = domain.description,
                startAt = domain.startAt,
                endAt = domain.endAt,
                isCompleted = domain.isCompleted,
            )

            override val color: Color
                @Composable get() =
                    if (!isCompleted) CalendarFlamingo else MediumGray
        }

        companion object {
            const val COMPLETE = "(완료) "
        }
    }
}
