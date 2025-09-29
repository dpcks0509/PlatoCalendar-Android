package pnu.plato.calendar.presentation.todo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import pnu.plato.calendar.presentation.common.theme.White
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ToDoScheduleItem(
    schedule: ScheduleUiModel,
    modifier: Modifier = Modifier,
    toggleCompletion: (Long, Boolean) -> Unit = { _, _ -> }
) {
    var isCompleted by remember {
        mutableStateOf(
            when (schedule) {
                is PersonalScheduleUiModel -> schedule.isCompleted
                is AcademicScheduleUiModel -> false
            }
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(schedule.color),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            val title = schedule.title.run {
                if (schedule is CourseScheduleUiModel) {
                    if (schedule.courseName.isEmpty()) this else "${schedule.courseName}_$this"
                } else {
                    this
                }
            }

            Text(text = title, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(schedule.color)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    val type = when (schedule) {
                        is AcademicScheduleUiModel -> "학사 일정"
                        is CourseScheduleUiModel -> "강의 일정"
                        is CustomScheduleUiModel -> "개인 일정"
                    }

                    Text(
                        text = type,
                        fontSize = 12.sp,
                        color = White,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = dateRange(schedule))
            }
        }

        if (schedule is PersonalScheduleUiModel) {
            Spacer(modifier = Modifier.weight(1f))

            Checkbox(
                checked = isCompleted,
                colors = CheckboxDefaults.colors(
                    checkedColor = PrimaryColor,
                    checkmarkColor = White,
                ),
                onCheckedChange = {
                    toggleCompletion(schedule.id, !schedule.isCompleted)
                    isCompleted = !isCompleted
                },
            )
        }
    }
}

private fun dateRange(schedule: ScheduleUiModel): String =
    when (schedule) {
        is AcademicScheduleUiModel -> formatDateRange(schedule.startAt, schedule.endAt)
        is PersonalScheduleUiModel -> formatDateRange(
            schedule.startAt.toLocalDate(),
            schedule.endAt.toLocalDate()
        )
    }

private fun formatDateRange(startAt: LocalDate, endAt: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    return "${startAt.format(formatter)} ~ ${endAt.format(formatter)}"
}

@Preview(showBackground = true)
@Composable
private fun ToDoScheduleItemPreview() {
    PlatoCalendarTheme {
        ToDoScheduleItem(
            schedule = CustomScheduleUiModel(
                id = 1L,
                title = "새해 계획 세우기",
                description = "",
                startAt = LocalDateTime.of(2024, 1, 11, 14, 0),
                endAt = LocalDateTime.now().plusDays(2).plusHours(3),
                isCompleted = false,
            ), modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}