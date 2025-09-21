package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pnu.plato.calendar.presentation.PlatoCalendarActivity.Companion.today
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.common.theme.LightGrey
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import java.time.LocalDateTime

private const val HAS_NO_SCHEDULE = "일정 없음"

@Composable
fun ScheduleItem(
    schedules: List<ScheduleUiModel>,
    onScheduleClick: (ScheduleUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    if (schedules.isEmpty()) {
        Text(
            text = HAS_NO_SCHEDULE,
            fontSize = 16.sp,
            color = LightGrey,
        )
    } else {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items = schedules) { schedule ->
                when (schedule) {
                    is AcademicScheduleUiModel -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(schedule.color)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = schedule.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    is PersonalScheduleUiModel -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(schedule.color)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = schedule.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = schedule.deadLine,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleItemPreview() {
    PlatoCalendarTheme {
        val sampleSchedules =
            listOf(
                AcademicScheduleUiModel(
                    title = "학사 일정",
                    startAt = today,
                    endAt = today,
                ),
                PersonalScheduleUiModel(
                    id = 0L,
                    title = "개인 일정",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                    courseName = "",
                ),
                PersonalScheduleUiModel(
                    id = 0L,
                    title = "개인 일정",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                    courseName = "",
                ),
                PersonalScheduleUiModel(
                    id = 0L,
                    title = "개인 일정",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                    courseName = "",
                ),
                PersonalScheduleUiModel(
                    id = 0L,
                    title = "개인 일정",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                    courseName = "",
                ),
            )

        ScheduleItem(
            schedules = sampleSchedules,
            onScheduleClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}