package pnu.plato.calendar.presentation.calendar

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pnu.plato.calendar.domain.entity.LoginStatus
import pnu.plato.calendar.domain.repository.CourseRepository
import pnu.plato.calendar.domain.repository.ScheduleRepository
import pnu.plato.calendar.presentation.PlatoCalendarActivity.Companion.today
import pnu.plato.calendar.presentation.calendar.component.MAX_DAY_SIZE
import pnu.plato.calendar.presentation.calendar.component.MAX_WEEK_SIZE
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.ChangeCurrentYearMonth
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.ChangeSelectedDate
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MakePersonalSchedule
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MoveToToday
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.RefreshSchedules
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.ShowScheduleDetail
import pnu.plato.calendar.presentation.calendar.intent.CalendarSideEffect
import pnu.plato.calendar.presentation.calendar.intent.CalendarSideEffect.ScrollToFirstMonth
import pnu.plato.calendar.presentation.calendar.intent.CalendarState
import pnu.plato.calendar.presentation.calendar.model.DaySchedule
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
    ) : BaseViewModel<CalendarState, CalendarEvent, CalendarSideEffect>(
            initialState = CalendarState(isLoading = true),
        ) {
        private val monthlyDates = mutableMapOf<YearMonth, List<List<LocalDate?>>>()
        private val monthlySchedules = mutableMapOf<YearMonth, List<SnapshotStateList<DaySchedule?>>>()

        init {
            viewModelScope.launch {
                loginManager.loginStatus.collect { loginStatus ->
                    getSchedules()
                }
            }
        }

        override suspend fun handleEvent(event: CalendarEvent) {
            when (event) {
                MoveToToday -> {
                    val previousSelectedDate = state.value.selectedDate
                    val todayYearMonth = YearMonth(year = today.year, month = today.monthValue)

                    deselectDate(previousSelectedDate)
                    selectDate(todayYearMonth, today)

                    setState {
                        copy(
                            selectedDate = today,
                            currentYearMonth = todayYearMonth,
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

                is ChangeSelectedDate -> {
                    val previousSelectedDate = state.value.selectedDate

                    deselectDate(previousSelectedDate)
                    selectDate(state.value.currentYearMonth, event.date)

                    setState { copy(selectedDate = event.date) }
                }

                is ChangeCurrentYearMonth -> {
                    setState { copy(currentYearMonth = event.yearMonth) }
                }

                RefreshSchedules -> refreshSchedules()

                is ShowScheduleDetail -> {
                    // TODO
                }
            }
        }

        fun getMonthSchedule(yearMonth: YearMonth): List<SnapshotStateList<DaySchedule?>> =
            monthlySchedules.getOrPut(yearMonth) {
                generateMonthSchedule(yearMonth)
            }

        private suspend fun getAcademicSchedules(): List<AcademicScheduleUiModel> {
            scheduleRepository
                .getAcademicSchedules()
                .onSuccess {
                    val academicSchedules = it.map(::AcademicScheduleUiModel)

                    return academicSchedules
                }.onFailure { throwable ->
                    ErrorEventBus.sendError(throwable.message)
                }

            return emptyList()
        }

        private suspend fun getPersonalSchedules(sessKey: String): List<PersonalScheduleUiModel> {
            scheduleRepository
                .getPersonalSchedules(sessKey = sessKey)
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

                    return personalSchedules
                }.onFailure { throwable ->
                    setState {
                        copy(isLoading = false)
                    }

                    ErrorEventBus.sendError(throwable.message)
                }

            return emptyList()
        }

        private fun getSchedules() {
            viewModelScope.launch {
                when (val loginStatus = loginManager.loginStatus.value) {
                    is LoginStatus.Login -> {
                        setState { copy(isLoading = true) }

                        val academicSchedules = async { getAcademicSchedules() }.await()
                        val personalSchedules =
                            async { getPersonalSchedules(loginStatus.loginSession.sessKey) }.await()
                        val schedules = academicSchedules + personalSchedules

                        setState {
                            copy(schedules = schedules, isLoading = false)
                        }
                    }

                    LoginStatus.Logout -> {
                        setState { copy(isLoading = true) }

                        val academicSchedules = getAcademicSchedules()

                        setState {
                            copy(schedules = academicSchedules, isLoading = false)
                        }
                    }

                    LoginStatus.Uninitialized -> setState { copy(isLoading = false) }
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

        private fun getMonthDate(yearMonth: YearMonth): List<List<LocalDate?>> =
            monthlyDates.getOrPut(yearMonth) {
                generateMonthDate(yearMonth)
            }

        private fun generateMonthDate(yearMonth: YearMonth): List<List<LocalDate?>> {
            val baseDate = LocalDate.of(yearMonth.year, yearMonth.month, 1)
            val dayOfWeekValue = if (baseDate.dayOfWeek.value == 7) 0 else baseDate.dayOfWeek.value
            val firstDateOfMonth = baseDate.minusDays(dayOfWeekValue.toLong())

            val rangeEnd = today.plusYears(1).minusDays(1)

            val firstMonth = YearMonth(year = today.year, month = today.monthValue)
            val lastMonth = firstMonth.plusMonths(12)

            val firstMonthStart: LocalDate? =
                if (yearMonth == firstMonth) LocalDate.of(today.year, today.monthValue, 1) else null
            val lastMonthEnd: LocalDate? = if (yearMonth == lastMonth) rangeEnd else null

            return List(MAX_WEEK_SIZE) { weekOffset ->
                List(MAX_DAY_SIZE) { dayOffset ->
                    val date =
                        firstDateOfMonth.plusDays((weekOffset * MAX_DAY_SIZE + dayOffset).toLong())
                    val beforeStart = firstMonthStart?.let { date.isBefore(it) } ?: false
                    val afterEnd = lastMonthEnd?.let { date.isAfter(it) } ?: false
                    if (beforeStart || afterEnd) null else date
                }
            }
        }

        private fun generateMonthSchedule(yearMonth: YearMonth): List<SnapshotStateList<DaySchedule?>> =
            getMonthDate(yearMonth).map { week ->
                week.map { date -> if (date != null) createDay(date, yearMonth) else null }.toMutableStateList()
            }

        private fun createDay(
            date: LocalDate,
            yearMonth: YearMonth,
        ): DaySchedule {
            val isToday = date == today
            val isSelected = date == state.value.selectedDate
            val isInMonth =
                date.year == yearMonth.year && date.monthValue == yearMonth.month
            val daySchedules =
                state.value.schedules.filter { schedule ->
                    when (schedule) {
                        is AcademicScheduleUiModel -> date == schedule.endAt
                        is PersonalScheduleUiModel -> date == schedule.endAt.toLocalDate()
                    }
                }

            return DaySchedule(
                date = date,
                isToday = isToday,
                isSelected = isSelected,
                isInMonth = isInMonth,
                schedules = daySchedules,
            )
        }

        private fun refreshSchedules() {
            val groupedByDate: Map<LocalDate, List<pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel>> =
                state.value.schedules.groupBy { schedule ->
                    when (schedule) {
                        is AcademicScheduleUiModel -> schedule.endAt
                        is PersonalScheduleUiModel -> schedule.endAt.toLocalDate()
                    }
                }

            monthlySchedules.values.forEach { monthSchedule ->
                monthSchedule.forEach { weekSchedule ->
                    weekSchedule.forEachIndexed { index, daySchedule ->
                        val newSchedules = groupedByDate[daySchedule?.date].orEmpty()
                        if (daySchedule?.schedules != newSchedules) {
                            weekSchedule[index] = daySchedule?.copy(schedules = newSchedules)
                        }
                    }
                }
            }
        }

        private fun deselectDate(date: LocalDate) {
            monthlySchedules.values.flatten().forEach { weekSchedule ->
                weekSchedule.find { it?.date == date }?.let { matched ->
                    val index = weekSchedule.indexOf(matched)
                    weekSchedule[index] = matched.copy(isSelected = false)
                }
            }
        }

        private fun selectDate(
            yearMonth: YearMonth,
            date: LocalDate,
        ) {
            monthlySchedules[yearMonth]?.forEach { weekSchedule ->
                weekSchedule.find { it?.date == date }?.let { matched ->
                    val index = weekSchedule.indexOf(matched)
                    weekSchedule[index] = matched.copy(isSelected = true)
                }
            }
        }
    }
