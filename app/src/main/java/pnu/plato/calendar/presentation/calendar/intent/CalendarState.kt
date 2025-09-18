package pnu.plato.calendar.presentation.calendar.intent

import pnu.plato.calendar.presentation.calendar.component.MAX_DAY_SIZE
import pnu.plato.calendar.presentation.calendar.component.MAX_MONTH_SIZE
import pnu.plato.calendar.presentation.calendar.component.MAX_WEEK_SIZE
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.base.UiState
import java.time.LocalDate

data class CalendarState(
    val today: LocalDate = LocalDate.now(),
    val selectedDate: LocalDate = today,
    val currentYearMonth: YearMonth = YearMonth(year = today.year, month = today.monthValue),
    val schedules: List<ScheduleUiModel> = emptyList(),
    val isLoading: Boolean = false,
) : UiState {
    val monthlyDates: Map<YearMonth, List<List<LocalDate>>> = run {
        val result = mutableMapOf<YearMonth, List<List<LocalDate>>>()

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
