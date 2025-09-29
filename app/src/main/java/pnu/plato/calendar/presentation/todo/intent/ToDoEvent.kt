package pnu.plato.calendar.presentation.todo.intent

import pnu.plato.calendar.presentation.common.base.UiEvent
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule

sealed interface ToDoEvent : UiEvent {
    data class TogglePersonalScheduleCompletion(
        val id: Long,
        val isCompleted: Boolean,
    ) : ToDoEvent

    data class ShowScheduleBottomSheet(
        val schedule: ScheduleUiModel? = null,
    ) : ToDoEvent

    data object HideScheduleBottomSheet : ToDoEvent

    data class EditCustomSchedule(
        val schedule: CustomSchedule,
    ) : ToDoEvent

    data class DeleteCustomSchedule(val id: Long) : ToDoEvent
}