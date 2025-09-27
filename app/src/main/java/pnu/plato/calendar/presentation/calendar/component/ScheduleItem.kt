package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pnu.plato.calendar.presentation.PlatoCalendarActivity.Companion.today
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pnu.plato.calendar.presentation.common.extension.noRippleClickable
import pnu.plato.calendar.presentation.common.theme.Gray
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.common.theme.White
import java.time.LocalDateTime

private const val HAS_NO_SCHEDULE = "일정 없음"

@Composable
fun ScheduleItem(
    schedules: List<ScheduleUiModel>,
    onScheduleClick: (ScheduleUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (schedules.isEmpty()) {
        Box(
            modifier =
                modifier
                    .fillMaxHeight()
                    .padding(end = 44.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = HAS_NO_SCHEDULE,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Gray,
            )
        }
    } else {
        val sortedSchedule =
            schedules.sortedWith(
                compareBy(
                    { if (it is AcademicScheduleUiModel) 0 else 1 },
                    { if (it is PersonalScheduleUiModel) it.isCompleted else false },
                    {
                        when (it) {
                            is AcademicScheduleUiModel -> it.endAt.atStartOfDay()
                            is PersonalScheduleUiModel -> it.endAt
                        }
                    },
                ),
            )

        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(items = sortedSchedule) { schedule ->
                when (schedule) {
                    is AcademicScheduleUiModel -> {
                        AcademicScheduleItem(schedule = schedule, onScheduleClick = onScheduleClick)
                    }

                    is PersonalScheduleUiModel -> {
                        PersonalScheduleItem(schedule = schedule, onScheduleClick = onScheduleClick)
                    }
                }
            }
        }
    }
}

@Composable
fun AcademicScheduleItem(
    schedule: AcademicScheduleUiModel,
    onScheduleClick: (ScheduleUiModel) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(schedule.color)
                .noRippleClickable { onScheduleClick(schedule) }
                .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(
            text = schedule.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = White,
        )
    }
}

@Composable
private fun PersonalScheduleItem(
    schedule: PersonalScheduleUiModel,
    onScheduleClick: (ScheduleUiModel) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(schedule.color)
                .noRippleClickable { onScheduleClick(schedule) }
                .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        val title = schedule.title.run {
            if (schedule is CourseScheduleUiModel) {
                if (schedule.courseName.isEmpty()) this else "${schedule.courseName}_$this"
            } else {
                this
            }
        }

        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = White,
        )
        Text(
            text = schedule.deadLine,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = White,
        )
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
                CourseScheduleUiModel(
                    id = 0L,
                    title = "개인 일정",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                    courseName = "운영체제",
                    isCompleted = false,
                ),
                CourseScheduleUiModel(
                    id = 0L,
                    title = "개인 일정",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                    courseName = "네트워크",
                    isCompleted = false,
                ),
                CustomScheduleUiModel(
                    id = 0L,
                    title = "개인 일정",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                    isCompleted = false,
                ),
                CustomScheduleUiModel(
                    id = 0L,
                    title = "개인 일정",
                    description = "",
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now(),
                    isCompleted = false,
                ),
            )

        ScheduleItem(
            schedules = sampleSchedules,
            onScheduleClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
