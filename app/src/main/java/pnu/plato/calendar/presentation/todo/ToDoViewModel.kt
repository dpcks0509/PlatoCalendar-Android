package pnu.plato.calendar.presentation.todo

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pnu.plato.calendar.domain.repository.CourseRepository
import pnu.plato.calendar.domain.repository.ScheduleRepository
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pnu.plato.calendar.presentation.common.base.BaseViewModel
import pnu.plato.calendar.presentation.common.eventbus.SnackbarEventBus
import pnu.plato.calendar.presentation.common.manager.CalendarScheduleManager
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent.TogglePersonalScheduleCompletion
import pnu.plato.calendar.presentation.todo.intent.ToDoSideEffect
import pnu.plato.calendar.presentation.todo.intent.ToDoState
import javax.inject.Inject

@HiltViewModel
class ToDoViewModel
    @Inject
    constructor(
        private val calendarScheduleManager: CalendarScheduleManager,
        private val scheduleRepository: ScheduleRepository,
        private val courseRepository: CourseRepository,
    ) : BaseViewModel<ToDoState, ToDoEvent, ToDoSideEffect>(initialState = ToDoState()) {
        init {
            viewModelScope.launch {
                launch {
                    calendarScheduleManager.schedules.collect { schedules ->
                        setState { copy(schedules = schedules) }
                    }
                }
            }
        }

        override suspend fun handleEvent(event: ToDoEvent) {
            when (event) {
                is TogglePersonalScheduleCompletion -> togglePersonalScheduleCompletion(event.id, event.isCompleted)
            }
        }

        private suspend fun togglePersonalScheduleCompletion(
            id: Long,
            isCompleted: Boolean,
        ) {
            val currentSchedule =
                state.value.schedules
                    .filterIsInstance<PersonalScheduleUiModel>()
                    .find { it.id == id } ?: return

            val personalSchedule =
                when (currentSchedule) {
                    is CourseScheduleUiModel -> {
                        val courseCode = courseRepository.getCourseCode(currentSchedule.courseName)

                        CourseSchedule(
                            id = currentSchedule.id,
                            title = currentSchedule.title,
                            description = currentSchedule.description,
                            startAt = currentSchedule.startAt,
                            endAt = currentSchedule.endAt,
                            isCompleted = isCompleted,
                            courseCode = courseCode,
                        )
                    }

                    is CustomScheduleUiModel ->
                        CustomSchedule(
                            id = currentSchedule.id,
                            title = currentSchedule.title,
                            description = currentSchedule.description,
                            startAt = currentSchedule.startAt,
                            endAt = currentSchedule.endAt,
                            isCompleted = isCompleted,
                        )
                }

            scheduleRepository
                .editPersonalSchedule(personalSchedule)
                .onSuccess {
                    calendarScheduleManager.updateSchedules(
                        state.value.schedules.map { schedule ->
                            if (schedule is PersonalScheduleUiModel && schedule.id == id) {
                                when (schedule) {
                                    is CourseScheduleUiModel -> schedule.copy(isCompleted = isCompleted)
                                    is CustomScheduleUiModel -> schedule.copy(isCompleted = isCompleted)
                                }
                            } else {
                                schedule
                            }
                        },
                    )
                    SnackbarEventBus.sendSuccess(if (isCompleted) "일정이 완료되었습니다." else "일정이 재개되었습니다.")
                }.onFailure { throwable ->
                    SnackbarEventBus.sendError(throwable.message)
                }
        }
    }
