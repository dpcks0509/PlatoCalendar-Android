package pnu.plato.calendar.presentation.todo.intent

import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pnu.plato.calendar.presentation.common.base.UiState
import pnu.plato.calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class ToDoState(
    val schedules: List<ScheduleUiModel> = emptyList(),
    val scheduleBottomSheetContent: ScheduleBottomSheetContent? = null,
    val isScheduleBottomSheetVisible: Boolean = false,
    val today: LocalDateTime,
) : UiState {
    private val currentTime = today
    private val todayDate = today.toLocalDate()

    private val validSchedules =
        schedules
            .filter { schedule ->
                when (schedule) {
                    is AcademicScheduleUiModel -> !schedule.endAt.isBefore(todayDate)
                    is PersonalScheduleUiModel -> schedule.endAt.isAfter(currentTime) && !schedule.isCompleted
                }
            }.sortedWith(
                compareBy(
                    { schedule ->
                        when (schedule) {
                            is AcademicScheduleUiModel -> schedule.endAt.atStartOfDay()
                            is PersonalScheduleUiModel -> schedule.endAt
                        }
                    },
                    { schedule ->
                        when (schedule) {
                            is AcademicScheduleUiModel -> schedule.startAt.atStartOfDay()
                            else -> null
                        }
                    },
                ),
            )

    val within7Days =
        validSchedules.filter { schedule ->
            val daysUntilEnd =
                when (schedule) {
                    is AcademicScheduleUiModel -> ChronoUnit.DAYS.between(todayDate, schedule.endAt)
                    is PersonalScheduleUiModel -> ChronoUnit.DAYS.between(todayDate, schedule.endAt.toLocalDate())
                }
            daysUntilEnd in 0..7
        }

    val completedSchedules =
        schedules
            .filterIsInstance<PersonalScheduleUiModel>()
            .filter { it.isCompleted }
            .sortedByDescending { it.endAt }

    val courseSchedules = validSchedules.filterIsInstance<CourseScheduleUiModel>()

    val customSchedules = validSchedules.filterIsInstance<CustomScheduleUiModel>()

    val academicSchedules = validSchedules.filterIsInstance<AcademicScheduleUiModel>()
}
