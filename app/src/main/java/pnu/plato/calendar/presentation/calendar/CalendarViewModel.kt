package pnu.plato.calendar.presentation.calendar

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pnu.plato.calendar.domain.entity.LoginStatus
import pnu.plato.calendar.domain.repository.CalendarRepository
import pnu.plato.calendar.domain.repository.CourseRepository
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent
import pnu.plato.calendar.presentation.calendar.intent.CalendarSideEffect
import pnu.plato.calendar.presentation.calendar.intent.CalendarState
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.common.base.BaseViewModel
import pnu.plato.calendar.presentation.common.eventbus.ErrorEventBus
import pnu.plato.calendar.presentation.common.manager.LoginManager
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel
    @Inject
    constructor(
        private val loginManager: LoginManager,
        private val calendarRepository: CalendarRepository,
        private val courseRepository: CourseRepository,
    ) : BaseViewModel<CalendarState, CalendarEvent, CalendarSideEffect>(initialState = CalendarState()) {
        init {
            viewModelScope.launch {
                loginManager.loginStatus.collect { loginStatus ->
                    fetchSchedules()
                }
            }
        }

        override suspend fun handleEvent(event: CalendarEvent) {
//            when (event) {
//            }
        }

        private suspend fun fetchSchedules() {
            when (val loginStatus = loginManager.loginStatus.value) {
                is LoginStatus.Login -> {
                    setState { copy(isLoading = true) }

                    calendarRepository
                        .getSchedules(sessKey = loginStatus.loginSession.sessKey)
                        .onSuccess { schedules ->
                            setState {
                                copy(
                                    schedules =
                                        schedules.map { domain ->
                                            ScheduleUiModel(
                                                domain = domain,
                                                courseName = courseRepository.getCourseName(domain.courseCode),
                                            )
                                        },
                                    isLoading = false,
                                )
                            }
                        }.onFailure { throwable ->
                            setState {
                                copy(
                                    schedules = emptyList(),
                                    isLoading = false,
                                )
                            }

                            ErrorEventBus.sendError(throwable.message)
                        }
                }

                is LoginStatus.Logout ->
                    setState {
                        copy(
                            schedules = emptyList(),
                            isLoading = false,
                        )
                    }
            }
        }
    }
