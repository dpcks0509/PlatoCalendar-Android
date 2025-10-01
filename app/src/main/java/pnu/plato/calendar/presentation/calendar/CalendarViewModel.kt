package pnu.plato.calendar.presentation.calendar

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import pnu.plato.calendar.domain.entity.LoginCredentials
import pnu.plato.calendar.domain.entity.LoginStatus
import pnu.plato.calendar.domain.entity.Schedule.NewSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule.CourseSchedule
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pnu.plato.calendar.domain.repository.CourseRepository
import pnu.plato.calendar.domain.repository.ScheduleRepository
import pnu.plato.calendar.presentation.PlatoCalendarActivity.Companion.today
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.DeleteCustomSchedule
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.EditCustomSchedule
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.HideLoginDialog
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.HideScheduleBottomSheet
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MakeCustomSchedule
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MoveToToday
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.ShowScheduleBottomSheet
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.TogglePersonalScheduleCompletion
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.TryLogin
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.UpdateCurrentYearMonth
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
import pnu.plato.calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.AcademicScheduleContent
import pnu.plato.calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.CourseScheduleContent
import pnu.plato.calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.CustomScheduleContent
import pnu.plato.calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent.NewScheduleContent
import pnu.plato.calendar.presentation.common.eventbus.SnackbarEventBus
import pnu.plato.calendar.presentation.common.manager.CalendarScheduleManager
import pnu.plato.calendar.presentation.common.manager.LoginManager
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel
    @Inject
    constructor(
        private val loginManager: LoginManager,
        private val scheduleRepository: ScheduleRepository,
        private val courseRepository: CourseRepository,
        private val calendarScheduleManager: CalendarScheduleManager,
    ) : BaseViewModel<CalendarState, CalendarEvent, CalendarSideEffect>(
            initialState = CalendarState(),
        ) {
        init {
            viewModelScope.launch {
                launch {
                    loginManager.loginStatus.collect { loginStatus ->
                        getSchedules()
                    }
                }

                launch {
                    calendarScheduleManager.schedules.collect { schedules ->
                        setState { copy(schedules = schedules) }
                    }
                }
            }
        }

        override suspend fun handleEvent(event: CalendarEvent) {
            when (event) {
                MoveToToday -> {
                    val todayYearMonth = YearMonth(year = today.year, month = today.monthValue)

                    calendarScheduleManager.updateSelectedDate(today)

                    setState {
                        copy(
                            selectedDate = today,
                            currentYearMonth = todayYearMonth,
                        )
                    }
                }

                is MakeCustomSchedule -> makeCustomSchedule(event.schedule)

                is TryLogin -> {
                    val isLoginSuccess = loginManager.login(event.loginCredentials)
                    if (isLoginSuccess) setState { copy(isLoginDialogVisible = false) }
                }

                is EditCustomSchedule -> editCustomSchedule(event.schedule)

                is DeleteCustomSchedule -> deleteCustomSchedule(event.id)

                is TogglePersonalScheduleCompletion ->
                    togglePersonalScheduleCompletion(
                        event.id,
                        event.isCompleted,
                    )

                is UpdateSelectedDate -> {
                    calendarScheduleManager.updateSelectedDate(event.date)
                    setState { copy(selectedDate = event.date) }
                }

                is UpdateCurrentYearMonth -> {
                    setState { copy(currentYearMonth = event.yearMonth) }
                }

                is ShowScheduleBottomSheet -> showScheduleBottomSheet(event)

                HideScheduleBottomSheet ->
                    setState {
                        copy(
                            scheduleBottomSheetContent = null,
                            isScheduleBottomSheetVisible = false,
                        )
                    }

                HideLoginDialog -> setState { copy(isLoginDialogVisible = false) }
            }
        }

        fun getMonthSchedule(yearMonth: YearMonth): List<List<DaySchedule?>> = calendarScheduleManager.getMonthSchedule(yearMonth)

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
                    calendarScheduleManager.updateLoading(false)

                    SnackbarEventBus.sendError(throwable.message)
                }

            return emptyList()
        }

        private fun getSchedules() {
            viewModelScope.launch {
                when (val loginStatus = loginManager.loginStatus.value) {
                    is LoginStatus.Login -> {
                        calendarScheduleManager.updateLoading(true)

                        val (academicSchedules, personalSchedules) =
                            awaitAll(
                                async { getAcademicSchedules() },
                                async { getPersonalSchedules(loginStatus.loginSession.sessKey) },
                            )

                        val schedules = academicSchedules + personalSchedules

                        calendarScheduleManager.updateSchedules(schedules)
                        calendarScheduleManager.updateLoading(false)
                    }

                    LoginStatus.Logout -> {
                        calendarScheduleManager.updateLoading(true)

                        val academicSchedules = getAcademicSchedules()

                        calendarScheduleManager.updateSchedules(academicSchedules)
                        calendarScheduleManager.updateLoading(false)
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
                    val updatedSchedules = state.value.schedules + customSchedule
                    calendarScheduleManager.updateSchedules(updatedSchedules)

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
                    val updatedSchedules =
                        state.value.schedules.filter { schedule ->
                            !(schedule is PersonalScheduleUiModel && schedule.id == id)
                        }
                    calendarScheduleManager.updateSchedules(updatedSchedules)

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
                    val updatedSchedules =
                        state.value.schedules.map { schedule ->
                            if (schedule is PersonalScheduleUiModel && schedule.id == id) {
                                when (schedule) {
                                    is CourseScheduleUiModel -> schedule.copy(isCompleted = isCompleted)
                                    is CustomScheduleUiModel -> schedule.copy(isCompleted = isCompleted)
                                }
                            } else {
                                schedule
                            }
                        }
                    calendarScheduleManager.updateSchedules(updatedSchedules)

                    setSideEffect { CalendarSideEffect.HideScheduleBottomSheet }
                    SnackbarEventBus.sendSuccess(if (isCompleted) "일정이 완료되었습니다." else "일정이 재개되었습니다.")
                }.onFailure { throwable ->
                    SnackbarEventBus.sendError(throwable.message)
                }
        }

        private fun showScheduleBottomSheet(event: ShowScheduleBottomSheet) {
            val schedule = event.schedule
            val isLoggedIn = loginManager.loginStatus.value is LoginStatus.Login

            val bottomSheetContent =
                when (schedule) {
                    is CourseScheduleUiModel -> CourseScheduleContent(schedule)
                    is CustomScheduleUiModel -> CustomScheduleContent(schedule)
                    is AcademicScheduleUiModel -> AcademicScheduleContent(schedule)
                    null -> NewScheduleContent
                }

            if (isLoggedIn || schedule is AcademicScheduleUiModel) {
                setState {
                    copy(
                        scheduleBottomSheetContent = bottomSheetContent,
                        isScheduleBottomSheetVisible = true,
                    )
                }
            } else {
                setState { copy(isLoginDialogVisible = true) }
            }
        }

        suspend fun tryLogin(credentials: LoginCredentials): Boolean {
            val isLoginSuccess = loginManager.login(credentials)
            if (isLoginSuccess) setState { copy(isLoginDialogVisible = false) }
            return isLoginSuccess
        }
    }
