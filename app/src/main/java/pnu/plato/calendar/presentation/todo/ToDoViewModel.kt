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
import pnu.plato.calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent
import pnu.plato.calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.AcademicScheduleContent
import pnu.plato.calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.CourseScheduleContent
import pnu.plato.calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.CustomScheduleContent
import pnu.plato.calendar.presentation.common.eventbus.ToastEventBus
import pnu.plato.calendar.presentation.common.manager.CalendarScheduleManager
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent.DeleteCustomSchedule
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent.EditCustomSchedule
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent.HideScheduleBottomSheet
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent.Refresh
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent.ShowScheduleBottomSheet
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent.TogglePersonalScheduleCompletion
import pnu.plato.calendar.presentation.todo.intent.ToDoSideEffect
import pnu.plato.calendar.presentation.todo.intent.ToDoState
import javax.inject.Inject
import pnu.plato.calendar.presentation.todo.intent.ToDoSideEffect.HideScheduleBottomSheet as ToDoHideSheet

@HiltViewModel
class ToDoViewModel
    @Inject
    constructor(
        private val calendarScheduleManager: CalendarScheduleManager,
        private val scheduleRepository: ScheduleRepository,
        private val courseRepository: CourseRepository,
    ) : BaseViewModel<ToDoState, ToDoEvent, ToDoSideEffect>(
            initialState = ToDoState(today = calendarScheduleManager.today.value),
        ) {
        val today get() = calendarScheduleManager.today

        init {
            viewModelScope.launch {
                launch {
                    calendarScheduleManager.today.collect { currentToday ->
                        setState { copy(today = currentToday) }
                    }
                }
                launch {
                    calendarScheduleManager.schedules.collect { schedules ->
                        setState { copy(schedules = schedules) }
                    }
                }
            }
        }

        override suspend fun handleEvent(event: ToDoEvent) {
            when (event) {
                is Refresh -> refresh()
                is TogglePersonalScheduleCompletion -> togglePersonalScheduleCompletion(event.id, event.isCompleted)
                is ShowScheduleBottomSheet -> showScheduleBottomSheet(event)
                is HideScheduleBottomSheet -> hideScheduleBottomSheet()
                is EditCustomSchedule -> editCustomSchedule(event.schedule)
                is DeleteCustomSchedule -> deleteCustomSchedule(event.id)
            }
        }

        private fun refresh() {
            viewModelScope.launch {
                calendarScheduleManager.updateToday()
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
                    setSideEffect { ToDoHideSheet }
                    ToastEventBus.sendSuccess(if (isCompleted) "일정이 완료되었습니다." else "일정이 재개되었습니다.")
                }.onFailure { throwable ->
                    ToastEventBus.sendError(throwable.message)
                }
        }

        private fun showScheduleBottomSheet(event: ShowScheduleBottomSheet) {
            val content: ScheduleBottomSheetContent =
                when (val schedule = event.schedule) {
                    is CourseScheduleUiModel -> CourseScheduleContent(schedule)
                    is CustomScheduleUiModel -> CustomScheduleContent(schedule)
                    is pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel ->
                        AcademicScheduleContent(
                            schedule,
                        )

                    null -> ScheduleBottomSheetContent.NewScheduleContent
                }

            setState {
                copy(
                    scheduleBottomSheetContent = content,
                    isScheduleBottomSheetVisible = true,
                )
            }
        }

        private fun hideScheduleBottomSheet() {
            setState {
                copy(
                    scheduleBottomSheetContent = null,
                    isScheduleBottomSheetVisible = false,
                )
            }
        }

        private suspend fun editCustomSchedule(customSchedule: CustomSchedule) {
            scheduleRepository
                .editPersonalSchedule(customSchedule)
                .onSuccess {
                    val updatedSchedules =
                        state.value.schedules.map { schedule ->
                            if (schedule is CustomScheduleUiModel && schedule.id == customSchedule.id) {
                                schedule.copy(
                                    title = customSchedule.title,
                                    description = customSchedule.description,
                                    startAt = customSchedule.startAt,
                                    endAt = customSchedule.endAt,
                                    isCompleted = customSchedule.isCompleted,
                                )
                            } else {
                                schedule
                            }
                        }
                    calendarScheduleManager.updateSchedules(updatedSchedules)

                    setSideEffect { ToDoHideSheet }
                    ToastEventBus.sendSuccess("일정이 수정되었습니다.")
                }.onFailure { throwable ->
                    ToastEventBus.sendError(throwable.message)
                }
        }

        private suspend fun deleteCustomSchedule(id: Long) {
            scheduleRepository
                .deleteCustomSchedule(id)
                .onSuccess {
                    val updatedSchedules =
                        state.value.schedules.filter { schedule ->
                            !(schedule is PersonalScheduleUiModel && schedule.id == id)
                        }
                    calendarScheduleManager.updateSchedules(updatedSchedules)

                    setSideEffect { ToDoHideSheet }
                    ToastEventBus.sendSuccess("일정이 삭제되었습니다.")
                }.onFailure { throwable ->
                    ToastEventBus.sendError(throwable.message)
                }
        }
    }
