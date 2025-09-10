package pnu.plato.calendar.presentation.calendar

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import pnu.plato.calendar.domain.entity.LoginStatus
import pnu.plato.calendar.domain.repository.CourseRepository
import pnu.plato.calendar.domain.repository.ScheduleRepository
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent
import pnu.plato.calendar.presentation.calendar.intent.CalendarSideEffect
import pnu.plato.calendar.presentation.calendar.intent.CalendarState
import pnu.plato.calendar.presentation.calendar.model.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.common.base.BaseViewModel
import pnu.plato.calendar.presentation.common.eventbus.ErrorEventBus
import pnu.plato.calendar.presentation.common.manager.LoginManager
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
//            when (event) {
//            }
        }

        private suspend fun fetchAcademicSchedules() {
            when (loginManager.loginStatus.value) {
                is LoginStatus.Login -> {
                    setState { copy(isLoading = true) }

                    scheduleRepository
                        .getAcademicSchedules()
                        .onSuccess { academicSchedules ->
                            setState {
                                copy(
                                    academicSchedules = academicSchedules.map(::AcademicScheduleUiModel),
                                    isLoading = false,
                                )
                            }
                        }.onFailure { throwable ->
                            setState {
                                copy(
                                    academicSchedules = emptyList(),
                                    isLoading = false,
                                )
                            }

                            ErrorEventBus.sendError(throwable.message)
                        }
                }

                is LoginStatus.Logout ->
                    setState {
                        copy(
                            academicSchedules = emptyList(),
                            isLoading = false,
                        )
                    }
            }
        }

        private suspend fun fetchPersonalSchedules() {
            when (val loginStatus = loginManager.loginStatus.value) {
                is LoginStatus.Login -> {
                    setState { copy(isLoading = true) }

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
                                                    courseName = courseRepository.getCourseName(domain.courseCode),
                                                    isComplete = false, // TODO ROOM DB 에서 가져오기
                                                )
                                            },
                                    isLoading = false,
                                )
                            }
                        }.onFailure { throwable ->
                            setState {
                                copy(
                                    personalSchedules = emptyList(),
                                    isLoading = false,
                                )
                            }

                            ErrorEventBus.sendError(throwable.message)
                        }
                }

                is LoginStatus.Logout ->
                    setState {
                        copy(
                            personalSchedules = emptyList(),
                            isLoading = false,
                        )
                    }
            }
        }
    }
