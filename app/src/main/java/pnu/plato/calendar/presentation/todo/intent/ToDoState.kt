package pnu.plato.calendar.presentation.todo.intent

import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.common.base.UiState

data class ToDoState(
    val schedules: List<ScheduleUiModel> = emptyList(),
) : UiState