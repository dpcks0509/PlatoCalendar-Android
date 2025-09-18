package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    LazyColumn(
        modifier = modifier,
        userScrollEnabled = false,
    ) {
        this.items(
            items = monthDates,
            key = { weekDates -> weekDates.first() },
        ) { week ->
            WeekItem(
                weekDates = week,
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
        MonthItem(
            monthDates =
                listOf(
                    listOf(
                        LocalDate.of(2023, 12, 31),
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 1, 2),
                        LocalDate.of(2024, 1, 3),
                        LocalDate.of(2024, 1, 4),
                        LocalDate.of(2024, 1, 5),
                        LocalDate.of(2024, 1, 6),
                    ),
                    listOf(
                        LocalDate.of(2024, 1, 7),
                        LocalDate.of(2024, 1, 8),
                        LocalDate.of(2024, 1, 9),
                        LocalDate.of(2024, 1, 10),
                        LocalDate.of(2024, 1, 11),
                        LocalDate.of(2024, 1, 12),
                        LocalDate.of(2024, 1, 13),
                    ),
                ),
            today = LocalDate.of(2024, 1, 8),
            selectedDate = LocalDate.of(2024, 1, 11),
            currentYearMonth = YearMonth(2024, 1),
            schedules =
                listOf(
                    AcademicScheduleUiModel(
                        title = "신정",
                        startAt = LocalDate.of(2024, 1, 1),
                        endAt = LocalDate.of(2024, 1, 1),
                    ),
                    PersonalScheduleUiModel(
                        id = 1L,
                        title = "새해 계획 세우기",
                        description = "목표 설정하기",
                        startAt = LocalDateTime.of(2024, 1, 3, 14, 0),
                        endAt = LocalDateTime.of(2024, 1, 3, 16, 0),
                        courseName = null,
                    ),
                ),
            onClickDate = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
