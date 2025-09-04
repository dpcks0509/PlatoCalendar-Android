package pusan.university.plato_calendar.presentation.calendar

import dagger.hilt.android.lifecycle.HiltViewModel
import pusan.university.plato_calendar.domain.entity.LoginStatus
import pusan.university.plato_calendar.domain.repository.CalendarRepository
import pusan.university.plato_calendar.domain.repository.SubjectRepository
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarSideEffect
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarState
import pusan.university.plato_calendar.presentation.calendar.model.ScheduleUiModel
import pusan.university.plato_calendar.presentation.common.base.BaseViewModel
import pusan.university.plato_calendar.presentation.common.manager.LoginManager
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    val loginManager: LoginManager,
    private val calendarRepository: CalendarRepository,
    private val subjectRepository: SubjectRepository
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
                                        subjectName = fetchSubjectName(domain.categories)
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

    private fun fetchSubjectName(categories: String?): String {
        val subjectCode = categories?.split("_")[2]

        return subjectRepository.getSubjectName(subjectCode)
    }
}