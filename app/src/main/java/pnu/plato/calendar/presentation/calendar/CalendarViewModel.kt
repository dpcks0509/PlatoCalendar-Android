package pnu.plato.calendar.presentation.calendar

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import pnu.plato.calendar.domain.entity.LoginStatus
import pnu.plato.calendar.domain.entity.Schedule.NewSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pnu.plato.calendar.domain.repository.CourseRepository
import pnu.plato.calendar.domain.repository.ScheduleRepository
import pnu.plato.calendar.presentation.PlatoCalendarActivity.Companion.today
import pnu.plato.calendar.presentation.calendar.component.MAX_DAY_SIZE
import pnu.plato.calendar.presentation.calendar.component.MAX_WEEK_SIZE
import pnu.plato.calendar.presentation.calendar.component.bottomsheet.ScheduleBottomSheetContent
import pnu.plato.calendar.presentation.calendar.component.bottomsheet.ScheduleBottomSheetContent.AcademicScheduleContent
import pnu.plato.calendar.presentation.calendar.component.bottomsheet.ScheduleBottomSheetContent.CourseScheduleContent
import pnu.plato.calendar.presentation.calendar.component.bottomsheet.ScheduleBottomSheetContent.CustomScheduleContent
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.DeleteCustomSchedule
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.EditCustomSchedule
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.HideScheduleBottomSheet
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MakeCustomSchedule
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MoveToToday
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.ShowScheduleBottomSheet
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.TogglePersonalScheduleCompletion
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.UpdateCurrentYearMonth
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.UpdateSchedules
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.UpdateSelectedDate
import pnu.plato.calendar.presentation.calendar.intent.CalendarSideEffect
import pnu.plato.calendar.presentation.calendar.intent.CalendarState
import pnu.plato.calendar.presentation.calendar.model.DaySchedule
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.base.BaseViewModel
import pnu.plato.calendar.presentation.common.eventbus.SnackbarEventBus
import pnu.plato.calendar.presentation.common.manager.LoginManager
import java.time.LocalDate
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

                    deselectDate(date = previousSelectedDate)
                    selectDate(date = today)

                    setState {
                        copy(
                            selectedDate = today,
                            currentYearMonth = todayYearMonth,
                        )
                    }
                }

                is MakeCustomSchedule -> makeCustomSchedule(event.schedule)

                is EditCustomSchedule -> editCustomSchedule(event.schedule)

                is DeleteCustomSchedule -> deleteCustomSchedule(event.id)

                is TogglePersonalScheduleCompletion -> togglePersonalScheduleCompletion(event.id, event.isCompleted)

                is UpdateSelectedDate -> {
                    val previousSelectedDate = state.value.selectedDate

                    deselectDate(date = previousSelectedDate)
                    selectDate(date = event.date)

                    setState { copy(selectedDate = event.date) }
                }

                is UpdateCurrentYearMonth -> {
                    setState { copy(currentYearMonth = event.yearMonth) }
                }

                is UpdateSchedules -> updateSchedules()

                is ShowScheduleBottomSheet ->
                    setState {
                        val scheduleBottomSheetContent =
                            when (event.schedule) {
                                is AcademicScheduleUiModel -> AcademicScheduleContent(event.schedule)
                                is CourseScheduleUiModel -> CourseScheduleContent(event.schedule)
                                is CustomScheduleUiModel -> CustomScheduleContent(event.schedule)
                                else -> ScheduleBottomSheetContent.NewScheduleContent
                            }

                        copy(
                            scheduleBottomSheetContent = scheduleBottomSheetContent,
                            isScheduleBottomSheetVisible = true,
                        )
                    }

                is HideScheduleBottomSheet ->
                    setState {
                        copy(
                            scheduleBottomSheetContent = null,
                            isScheduleBottomSheetVisible = false,
                        )
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
                    SnackbarEventBus.sendError(throwable.message)
                }

            return emptyList()
        }

        private suspend fun getPersonalSchedules(sessKey: String): List<ScheduleUiModel> {
            scheduleRepository
                .getPersonalSchedules(sessKey = sessKey)
                .onSuccess {
                    val personalSchedules =
                        it.map { domain ->
                            when (domain) {
                                is CourseSchedule -> {
                                    val courseName =
                                        courseRepository.getCourseName(
                                            domain.courseCode,
                                        )

                                    CourseScheduleUiModel(
                                        domain = domain,
                                        courseName = courseName,
                                    )
                                }

                                is CustomSchedule -> CustomScheduleUiModel(domain)
                            }
                        }

                    return personalSchedules
                }.onFailure { throwable ->
                    setState {
                        copy(isLoading = false)
                    }

                    SnackbarEventBus.sendError(throwable.message)
                }

            return emptyList()
        }

        private fun getSchedules() {
            viewModelScope.launch {
                when (val loginStatus = loginManager.loginStatus.value) {
                    is LoginStatus.Login -> {
                        setState { copy(isLoading = true) }

                        val (academicSchedules, personalSchedules) =
                            awaitAll(
                                async { getAcademicSchedules() },
                                async { getPersonalSchedules(loginStatus.loginSession.sessKey) },
                            )

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

                    LoginStatus.Uninitialized -> Unit
                }
            }
        }

        private suspend fun makeCustomSchedule(newSchedule: NewSchedule) {
            scheduleRepository
                .makeCustomSchedule(newSchedule)
                .onSuccess { id ->
                    val customSchedule =
                        CustomScheduleUiModel(
                            id = id,
                            title = newSchedule.title,
                            description = newSchedule.description,
                            startAt = newSchedule.startAt,
                            endAt = newSchedule.endAt,
                            isCompleted = false,
                        )
                    setState {
                        copy(schedules = schedules + customSchedule)
                    }

                    setSideEffect { CalendarSideEffect.HideScheduleBottomSheet }
                    SnackbarEventBus.sendSuccess("일정이 생성되었습니다.")
                }.onFailure { throwable ->
                    SnackbarEventBus.sendError(throwable.message)
                }
        }

        private suspend fun editCustomSchedule(customSchedule: CustomSchedule) {
            scheduleRepository
                .editPersonalSchedule(customSchedule)
                .onSuccess {
                    setState {
                        copy(
                            schedules =
                                schedules.map { schedule ->
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
                                },
                        )
                    }

                    setSideEffect { CalendarSideEffect.HideScheduleBottomSheet }
                    SnackbarEventBus.sendSuccess("일정이 수정되었습니다.")
                }.onFailure { throwable ->
                    SnackbarEventBus.sendError(throwable.message)
                }
        }

        private suspend fun deleteCustomSchedule(id: Long) {
            scheduleRepository
                .deleteCustomSchedule(id)
                .onSuccess {
                    setState {
                        copy(
                            schedules =
                                schedules.filter { schedule ->
                                    !(schedule is PersonalScheduleUiModel && schedule.id == id)
                                },
                        )
                    }

                    setSideEffect { CalendarSideEffect.HideScheduleBottomSheet }
                    SnackbarEventBus.sendSuccess("일정이 삭제되었습니다.")
                }.onFailure { throwable ->
                    SnackbarEventBus.sendError(throwable.message)
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
                    setState {
                        copy(
                            schedules =
                                schedules.map { schedule ->
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
                    }

                    setSideEffect { CalendarSideEffect.HideScheduleBottomSheet }
                    SnackbarEventBus.sendSuccess(if (isCompleted) "일정이 완료되었습니다." else "일정 완료가 해제되었습니다.")
                }.onFailure { throwable ->
                    SnackbarEventBus.sendError(throwable.message)
                }
        }

    private fun updateSchedules() {
        val groupedByDate: Map<LocalDate, List<ScheduleUiModel>> =
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

    private fun selectDate(date: LocalDate) {
        monthlySchedules.values.flatten().forEach { weekSchedule ->
            weekSchedule.find { it?.date == date }?.let { matched ->
                val index = weekSchedule.indexOf(matched)
                weekSchedule[index] = matched.copy(isSelected = true)
            }
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
                week
                    .map { date -> if (date != null) createDay(date, yearMonth) else null }
                    .toMutableStateList()
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
    }
