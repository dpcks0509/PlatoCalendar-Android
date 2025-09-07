package pnu.plato.calendar.presentation.calendar

import dagger.hilt.android.lifecycle.HiltViewModel
import pnu.plato.calendar.domain.entity.LoginStatus
import pnu.plato.calendar.domain.repository.CalendarRepository
import pnu.plato.calendar.domain.repository.CourseRepository
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent
import pnu.plato.calendar.presentation.calendar.intent.CalendarSideEffect
import pnu.plato.calendar.presentation.calendar.intent.CalendarState
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.common.base.BaseViewModel
import pnu.plato.calendar.presentation.common.manager.LoginManager
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    val loginManager: LoginManager,
    private val calendarRepository: CalendarRepository,
    private val courseRepository: CourseRepository
) : BaseViewModel<CalendarState, CalendarEvent, CalendarSideEffect>(
    initialState = CalendarState(
        isLoading = true
    )
) {

    override suspend fun handleEvent(event: CalendarEvent) {
        when (event) {
            CalendarEvent.FetchSchedules -> fetchSchedules()
        }
    }

    private suspend fun fetchSchedules() {
        val currentLoginState = loginManager.loginStatus.value

        when (currentLoginState) {
            is LoginStatus.Login -> {
                calendarRepository.getSchedules(sessKey = currentLoginState.loginSession.sessKey)
                    .onSuccess { schedules ->
                        setState {
                            copy(
                                schedules = schedules.map { domain ->
                                    ScheduleUiModel(
                                        domain = domain,
                                        courseName = courseRepository.getCourseName(domain.courseCode)
                                    )
                                },
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }.onFailure { throwable ->
                        setState {
                            copy(
                                schedules = emptyList(),
                                isLoading = false,
                                errorMessage = throwable.message
                            )
                        }
                    }
            }

            is LoginStatus.Logout -> setState {
                copy(
                    schedules = emptyList(),
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }
}