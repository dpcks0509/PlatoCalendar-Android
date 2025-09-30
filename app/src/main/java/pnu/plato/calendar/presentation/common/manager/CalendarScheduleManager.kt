package pnu.plato.calendar.presentation.common.manager

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pnu.plato.calendar.presentation.PlatoCalendarActivity.Companion.today
import pnu.plato.calendar.presentation.calendar.component.MAX_DAY_SIZE
import pnu.plato.calendar.presentation.calendar.component.MAX_WEEK_SIZE
import pnu.plato.calendar.presentation.calendar.model.DaySchedule
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarScheduleManager
    @Inject
    constructor() {
        private val monthlyDates = mutableMapOf<YearMonth, List<List<LocalDate?>>>()
        private val monthlySchedules = mutableMapOf<YearMonth, List<MutableList<DaySchedule?>>>()
        private val selectedDate = MutableStateFlow(today)

        private val _schedules = MutableStateFlow<List<ScheduleUiModel>>(emptyList())
        val schedules: StateFlow<List<ScheduleUiModel>> = _schedules.asStateFlow()

        private val _isLoading = MutableStateFlow(true)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        fun updateSchedules(schedules: List<ScheduleUiModel>) {
            _schedules.value = schedules
            refreshMonthlySchedules()
        }

        fun updateLoading(isLoading: Boolean) {
            _isLoading.value = isLoading
        }

        fun updateSelectedDate(date: LocalDate) {
            val previousDate = selectedDate.value
            deselectDate(previousDate)
            selectDate(date)
            selectedDate.value = date
        }

        fun getMonthSchedule(yearMonth: YearMonth): List<List<DaySchedule?>> =
            monthlySchedules.getOrPut(yearMonth) {
                generateMonthSchedule(yearMonth)
            }

        private fun refreshMonthlySchedules() {
            val groupedByDate: Map<LocalDate, List<ScheduleUiModel>> =
                _schedules.value.groupBy { schedule ->
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
            val isSelected = date == selectedDate.value
            val isInMonth =
                date.year == yearMonth.year && date.monthValue == yearMonth.month
            val daySchedules =
                _schedules.value.filter { schedule ->
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
