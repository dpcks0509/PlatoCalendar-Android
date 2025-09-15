package pnu.plato.calendar.presentation.calendar

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import pnu.plato.calendar.domain.entity.LoginStatus
import pnu.plato.calendar.domain.repository.CourseRepository
import pnu.plato.calendar.domain.repository.ScheduleRepository
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.FetchPersonalSchedules
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MakePersonalSchedule
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MoveToToday
import pnu.plato.calendar.presentation.calendar.intent.CalendarSideEffect
import pnu.plato.calendar.presentation.calendar.intent.CalendarState
import pnu.plato.calendar.presentation.calendar.model.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.PersonalScheduleUiModel.Companion.COMPLETE
import pnu.plato.calendar.presentation.common.base.BaseViewModel
import pnu.plato.calendar.presentation.common.eventbus.ErrorEventBus
import pnu.plato.calendar.presentation.common.manager.LoginManager
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel
    @Inject
    constructor(
        private val loginManager: LoginManager,
        private val scheduleRepository: ScheduleRepository,
        private val courseRepository: CourseRepository,
    ) : BaseViewModel<CalendarState, CalendarEvent, CalendarSideEffect>(initialState = CalendarState()) {
        init {
            viewModelScope.launch {
                loginManager.loginStatus.collect { loginStatus ->
                    coroutineScope {
                        launch { fetchAcademicSchedules() }
                        launch { fetchPersonalSchedules() }
                    }
                }
            }
        }

        override suspend fun handleEvent(event: CalendarEvent) {
            when (event) {
                FetchPersonalSchedules -> fetchPersonalSchedules()

                MoveToToday -> {
                    val today = LocalDate.now()
                    if (today != state.value.today) {
                        setState { copy(today = today, selectedDate = today) }
                    } else {
                        setState { copy(selectedDate = today) }
                    }
                }

                is MakePersonalSchedule ->
                    makePersonalSchedule(
                        title = event.title,
                        description = event.description,
                        startAt = event.startAt,
                        endAt = event.endAt,
                    )
            }
        }

        private suspend fun fetchAcademicSchedules() {
            scheduleRepository
                .getAcademicSchedules()
                .onSuccess { academicSchedules ->
                    setState {
                        copy(academicSchedules = academicSchedules.map(::AcademicScheduleUiModel))
                    }
                }.onFailure { throwable ->
                    setState {
                        copy(academicSchedules = emptyList())
                    }

                    ErrorEventBus.sendError(throwable.message)
                }
        }

        private suspend fun fetchPersonalSchedules() {
            when (val loginStatus = loginManager.loginStatus.value) {
                is LoginStatus.Login -> {
                    scheduleRepository
                        .getPersonalSchedules(sessKey = loginStatus.loginSession.sessKey)
                        .onSuccess { personalSchedules ->
                            setState {
                                copy(
                                    personalSchedules =
                                        personalSchedules
                                            .map { domain ->
                                                PersonalScheduleUiModel(
                                                    domain = domain,
                                                    courseName =
                                                        courseRepository.getCourseName(
                                                            domain.courseCode,
                                                        ),
                                                )
                                            },
                                )
                            }
                        }.onFailure { throwable ->
                            setState {
                                copy(personalSchedules = emptyList())
                            }

                            ErrorEventBus.sendError(throwable.message)
                        }
                }

                is LoginStatus.Logout ->
                    setState {
                        copy(personalSchedules = emptyList())
                    }
            }
        }

        private suspend fun makePersonalSchedule(
            title: String,
            description: String?,
            startAt: LocalDateTime,
            endAt: LocalDateTime,
        ) {
            scheduleRepository
                .createPersonalSchedule(
                    title = title,
                    description = description,
                    startAt = startAt,
                    endAt = endAt,
                ).onSuccess {
                    val newSchedule =
                        PersonalScheduleUiModel(
                            id = System.currentTimeMillis(),
                            title = title,
                            description = description,
                            startAt = startAt,
                            endAt = endAt,
                            courseName = null,
                        )
                    setState {
                        copy(personalSchedules = personalSchedules + newSchedule)
                    }
                }.onFailure { throwable ->
                    ErrorEventBus.sendError(throwable.message)
                }
        }

        private suspend fun editPersonalSchedule(
            id: Long,
            title: String,
            description: String?,
            startAt: LocalDateTime,
            endAt: LocalDateTime,
        ) {
            scheduleRepository
                .updatePersonalSchedule(
                    id = id,
                    title = title,
                    description = description,
                    startAt = startAt,
                    endAt = endAt,
                ).onSuccess {
                    setState {
                        copy(
                            personalSchedules =
                                personalSchedules.map { schedule ->
                                    if (schedule.id == id) {
                                        schedule.copy(
                                            title = title,
                                            description = description,
                                            startAt = startAt,
                                            endAt = endAt,
                                        )
                                    } else {
                                        schedule
                                    }
                                },
                        )
                    }
                }.onFailure { throwable ->
                    ErrorEventBus.sendError(throwable.message)
                }
        }

        private suspend fun completePersonalSchedule(
            id: Long,
            title: String,
            description: String?,
            startAt: LocalDateTime,
            endAt: LocalDateTime,
        ) {
            scheduleRepository
                .updatePersonalSchedule(
                    id = id,
                    title = COMPLETE + title,
                    description = description,
                    startAt = startAt,
                    endAt = endAt,
                ).onSuccess {
                    setState {
                        copy(
                            personalSchedules =
                                personalSchedules.map { schedule ->
                                    if (schedule.id == id) {
                                        schedule.copy(title = COMPLETE + title)
                                    } else {
                                        schedule
                                    }
                                },
                        )
                    }
                }.onFailure { throwable ->
                    ErrorEventBus.sendError(throwable.message)
                }
        }

        private suspend fun unCompletePersonalSchedule(
            id: Long,
            title: String,
            description: String?,
            startAt: LocalDateTime,
            endAt: LocalDateTime,
        ) {
            scheduleRepository
                .updatePersonalSchedule(
                    id = id,
                    title = title.removePrefix(COMPLETE),
                    description = description,
                    startAt = startAt,
                    endAt = endAt,
                ).onSuccess {
                    setState {
                        copy(
                            personalSchedules =
                                personalSchedules.map { schedule ->
                                    if (schedule.id == id) {
                                        schedule.copy(title = title.removePrefix(COMPLETE))
                                    } else {
                                        schedule
                                    }
                                },
                        )
                    }
                }.onFailure { throwable ->
                    ErrorEventBus.sendError(throwable.message)
                }
        }

        private suspend fun deletePersonalSchedule(id: Long) {
            scheduleRepository
                .deletePersonalSchedule(id)
                .onSuccess {
                    setState {
                        copy(personalSchedules = personalSchedules.filter { it.id != id })
                    }
                }.onFailure { throwable ->
                    ErrorEventBus.sendError(throwable.message)
                }
        }
    }
