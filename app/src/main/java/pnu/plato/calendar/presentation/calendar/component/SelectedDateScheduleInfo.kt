package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pnu.plato.calendar.presentation.PlatoCalendarActivity.Companion.today
import pnu.plato.calendar.presentation.calendar.model.DayOfWeekUiModel.Companion.isWeekend
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pnu.plato.calendar.presentation.common.theme.Black
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import pnu.plato.calendar.presentation.common.theme.Red
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun SelectedDateScheduleInfo(
    selectedDate: LocalDate,
    schedules: List<ScheduleUiModel>,
    onScheduleClick: (ScheduleUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.width(28.dp),
        ) {
            Text(
                text = selectedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color =
                    if (selectedDate.dayOfWeek.isWeekend()) {
                        Red
                    } else {
                        Black
                    },
            )

            Box(
                modifier =
                    Modifier
                        .then(
                            if (selectedDate == today) {
                                Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryColor)
                            } else {
                                Modifier
                            },
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = selectedDate.dayOfMonth.toString(),
                    color =
                        if (selectedDate == today) {
                            Color.White
                        } else if (selectedDate.dayOfWeek.isWeekend()) {
                            Red
                        } else {
                            Black
                        },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        ScheduleItem(
            schedules = schedules,
            onScheduleClick = onScheduleClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SelectedDateScheduleInfoPreview() {
    PlatoCalendarTheme {
        val sampleSchedules =
            listOf(
                AcademicScheduleUiModel(
                    title = "학사 일정",
                    startAt = today,
                    endAt = today,
                ),
                CourseScheduleUiModel(
                    id = 0L,
                    title = "개인 일정",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                    courseName = "CB20125",
                ),
                CourseScheduleUiModel(
                    id = 0L,
                    title = "개인 일정",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                    courseName = "DS20438",
                ),
                CustomScheduleUiModel(
                    id = 0L,
                    title = "개인 일정",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                ),
                CustomScheduleUiModel(
                    id = 0L,
                    title = "개인 일정",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                ),
            )

        SelectedDateScheduleInfo(
            selectedDate = LocalDate.now(),
            schedules = sampleSchedules,
            onScheduleClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
