package pnu.plato.calendar.presentation.calendar

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pnu.plato.calendar.domain.entity.LoginStatus
import pnu.plato.calendar.domain.repository.CourseRepository
import pnu.plato.calendar.domain.repository.ScheduleRepository
import pnu.plato.calendar.presentation.calendar.component.MAX_DAY_SIZE
import pnu.plato.calendar.presentation.calendar.component.MAX_MONTH_SIZE
import pnu.plato.calendar.presentation.calendar.component.MAX_WEEK_SIZE
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.ChangeCurrentYearMonth
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.ChangeSelectedDate
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.GetPersonalSchedules
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MakePersonalSchedule
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MoveToToday
import pnu.plato.calendar.presentation.calendar.intent.CalendarSideEffect
import pnu.plato.calendar.presentation.calendar.intent.CalendarSideEffect.ScrollToFirstMonth
import pnu.plato.calendar.presentation.calendar.intent.CalendarState
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.Companion.COMPLETE
import pnu.plato.calendar.presentation.calendar.model.YearMonth
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
) : BaseViewModel<CalendarState, CalendarEvent, CalendarSideEffect>(initialState = CalendarState(isLoading = true)
) {
    init {
        viewModelScope.launch {
            loginManager.loginStatus.collect { loginStatus ->
                getPersonalSchedules()
            }
        }

        viewModelScope.launch {
            getAcademicSchedules()
        }
    }

    val monthlyDates: Map<YearMonth, List<List<LocalDate>>> = run {
        val result = mutableMapOf<YearMonth, List<List<LocalDate>>>()

        val today = state.value.today
        repeat(MAX_MONTH_SIZE) { monthOffset ->
            val targetDate =
                LocalDate.of(today.year, today.month, 1)
                    .plusMonths(monthOffset.toLong())
            val yearMonth = YearMonth(targetDate.year, targetDate.monthValue)
            val monthDates = generateMonthDates(yearMonth)

            result[yearMonth] = monthDates
        }

        result
    }

    override suspend fun handleEvent(event: CalendarEvent) {
        when (event) {
            GetPersonalSchedules -> getPersonalSchedules()

            MoveToToday -> {
                setState {
                    copy(
                        selectedDate = today,
                        currentYearMonth = YearMonth(year = today.year, month = today.monthValue)
                    )
                }

                setSideEffect { ScrollToFirstMonth }
            }

            is MakePersonalSchedule ->
                makePersonalSchedule(
                    title = event.title,
                    description = event.description,
                    startAt = event.startAt,
                    endAt = event.endAt,
                )

            is ChangeSelectedDate -> setState { copy(selectedDate = event.date) }

            is ChangeCurrentYearMonth -> setState { copy(currentYearMonth = event.yearMonth) }
        }
    }

    private suspend fun getAcademicSchedules() {
        scheduleRepository
            .getAcademicSchedules()
            .onSuccess {
                val academicSchedules = it.map(::AcademicScheduleUiModel)

                setState {
                    copy(schedules = state.value.schedules + academicSchedules)
                }
            }.onFailure { throwable ->
                ErrorEventBus.sendError(throwable.message)
            }
    }

    private suspend fun getPersonalSchedules() {
        val academicSchedules = state.value.schedules.filterIsInstance<AcademicScheduleUiModel>()

        when (val loginStatus = loginManager.loginStatus.value) {
            is LoginStatus.Login -> {
                setState { copy(isLoading = true) }

                scheduleRepository
                    .getPersonalSchedules(sessKey = loginStatus.loginSession.sessKey)
                    .onSuccess {
                        val personalSchedules =
                            it.map { domain ->
                                PersonalScheduleUiModel(
                                    domain = domain,
                                    courseName =
                                        courseRepository.getCourseName(
                                            domain.courseCode,
                                        ),
                                )
                            }

                        setState {
                            copy(
                                schedules = academicSchedules + personalSchedules,
                                isLoading = false
                            )
                        }
                    }.onFailure { throwable ->
                        setState {
                            copy(isLoading = false)
                        }

                        ErrorEventBus.sendError(throwable.message)
                    }
            }

            is LoginStatus.Logout -> {
                setState {
                    copy(schedules = academicSchedules, isLoading = false)
                }
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
            .makePersonalSchedule(
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
                    copy(schedules = schedules + newSchedule)
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
            .editPersonalSchedule(
                id = id,
                title = title,
                description = description,
                startAt = startAt,
                endAt = endAt,
            ).onSuccess {
                setState {
                    copy(
                        schedules =
                            schedules.map { schedule ->
                                if (schedule is PersonalScheduleUiModel && schedule.id == id) {
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
            .editPersonalSchedule(
                id = id,
                title = COMPLETE + title,
                description = description,
                startAt = startAt,
                endAt = endAt,
            ).onSuccess {
                setState {
                    copy(
                        schedules =
                            schedules.map { schedule ->
                                if (schedule is PersonalScheduleUiModel && schedule.id == id) {
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
            .editPersonalSchedule(
                id = id,
                title = title.removePrefix(COMPLETE),
                description = description,
                startAt = startAt,
                endAt = endAt,
            ).onSuccess {
                setState {
                    copy(
                        schedules =
                            schedules.map { schedule ->
                                if (schedule is PersonalScheduleUiModel && schedule.id == id) {
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
                    copy(
                        schedules =
                            schedules.filter { schedule ->
                                !(schedule is PersonalScheduleUiModel && schedule.id == id)
                            },
                    )
                }
            }.onFailure { throwable ->
                ErrorEventBus.sendError(throwable.message)
            }
    }

    private fun generateMonthDates(yearMonth: YearMonth): List<List<LocalDate>> {
        val monthDates = mutableListOf<List<LocalDate>>()

        val baseDate = LocalDate.of(yearMonth.year, yearMonth.month, 1)
        val dayOfWeekValue = if (baseDate.dayOfWeek.value == 7) 0 else baseDate.dayOfWeek.value
        val firstDateOfMonth = baseDate.minusDays(dayOfWeekValue.toLong())

        repeat(MAX_WEEK_SIZE) { weekOffset ->
            val week = mutableListOf<LocalDate>()

            repeat(MAX_DAY_SIZE) { dayOffset ->
                val date =
                    firstDateOfMonth.plusDays((weekOffset * MAX_DAY_SIZE + dayOffset).toLong())
                week.add(date)
            }
            monthDates.add(week)
        }

        return monthDates
    }
}
