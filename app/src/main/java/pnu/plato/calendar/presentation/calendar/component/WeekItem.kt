package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun WeekItem(
    weekDates: List<LocalDate>,
    today: LocalDate,
    selectedDate: LocalDate,
    currentYearMonth: YearMonth,
    schedules: List<ScheduleUiModel>,
    onClickDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        weekDates.forEach { date ->
            DayItem(
                date = date,
                today = today,
                selectedDate = selectedDate,
                currentYearMonth = currentYearMonth,
                schedules = schedules,
                onClickDate = { onClickDate(date) },
                modifier =
                    Modifier
                        .weight(1f)
                        .aspectRatio(3f / 3.6f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeekItemPreview() {
    PlatoCalendarTheme {
        val weekDates = List(7) { LocalDate.of(2024, 1, 7).plusDays(it.toLong()) }
        val schedules = List(7) { index ->
            PersonalScheduleUiModel(
                id = index.toLong(),
                title = "일정 $index",
                description = "",
                startAt = LocalDateTime.of(2024, 1, 7 + index, 10, 0),
                endAt = LocalDateTime.of(2024, 1, 7 + index, 12, 0),
                courseName = null
            )
        }

        WeekItem(
            weekDates = weekDates,
            today = weekDates[1],
            selectedDate = weekDates[3],
            currentYearMonth = YearMonth(2024, 1),
            schedules = schedules,
            onClickDate = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}