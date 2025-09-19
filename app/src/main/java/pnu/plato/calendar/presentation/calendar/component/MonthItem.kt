package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun MonthItem(
    monthDates: List<List<LocalDate>>,
    today: LocalDate,
    selectedDate: LocalDate,
    currentYearMonth: YearMonth,
    schedules: List<ScheduleUiModel>,
    onClickDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        monthDates.forEach { weekDates ->
            WeekItem(
                weekDates = weekDates,
                today = today,
                selectedDate = selectedDate,
                currentYearMonth = currentYearMonth,
                schedules = schedules,
                onClickDate = onClickDate,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MonthItemPreview() {
    PlatoCalendarTheme {
        val monthDates = List(6) { week ->
            List(7) { day ->
                LocalDate.of(2024, 1, 1).minusDays(1).plusDays((week * 7 + day).toLong())
            }
        }

        val schedules = listOf(
            AcademicScheduleUiModel(
                title = "신정",
                startAt = LocalDate.of(2024, 1, 1),
                endAt = LocalDate.of(2024, 1, 1),
            ),
            PersonalScheduleUiModel(
                id = 1L,
                title = "새해 계획 세우기",
                description = "",
                startAt = LocalDateTime.of(2024, 1, 3, 14, 0),
                endAt = LocalDateTime.of(2024, 1, 3, 16, 0),
                courseName = null,
            ),
        )

        MonthItem(
            monthDates = monthDates,
            today = LocalDate.of(2024, 1, 8),
            selectedDate = LocalDate.of(2024, 1, 11),
            currentYearMonth = YearMonth(2024, 1),
            schedules = schedules,
            onClickDate = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}