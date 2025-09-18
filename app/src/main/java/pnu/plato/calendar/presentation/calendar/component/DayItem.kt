package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pnu.plato.calendar.presentation.calendar.model.DayUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import java.time.LocalDate
import java.time.LocalDateTime

private const val MAX_SCHEDULES_SIZE = 5

@Composable
fun DayItem(
    day: DayUiModel,
    onClickDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { onClickDate(day.date) },
                ).then(
                    if (day.isSelected) {
                        Modifier
                            .clip(
                                RoundedCornerShape(12.dp),
                            ).background(LightGray)
                    } else {
                        Modifier
                    },
                ).padding(top = 6.dp, bottom = 12.dp, start = 6.dp, end = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier =
                Modifier
                    .then(
                        if (day.isToday) {
                            Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(PrimaryColor)
                        } else {
                            Modifier
                        },
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color =
                    (
                        if (day.isWeekend) {
                            Color.Red
                        } else if (day.isToday) {
                            Color.White
                        } else {
                            Color.Black
                        }
                    ).let { color -> if (day.isInMonth) color else color.copy(alpha = 0.6f) },
                fontSize = 14.sp,
                fontWeight = if (day.isInMonth) FontWeight.Bold else FontWeight.Normal,
            )
        }

        val schedules =
            day.schedules
                .filter { schedule ->
                    !(schedule is PersonalScheduleUiModel && schedule.isComplete)
                }.sortedBy { schedule ->
                    when (schedule) {
                        is AcademicScheduleUiModel -> 0
                        is PersonalScheduleUiModel -> 1
                    }
                }.take(MAX_SCHEDULES_SIZE)

        LazyRow(
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(1.5.dp, Alignment.CenterHorizontally),
            userScrollEnabled = false,
        ) {
            items(items = schedules) { schedule ->
                Box(
                    modifier =
                        Modifier
                            .size(4.5.dp)
                            .clip(CircleShape)
                            .background(schedule.color),
                )
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun DayItemPreview() {
    PlatoCalendarTheme {
        DayItem(
            day =
                DayUiModel(
                    date = LocalDate.now(),
                    isToday = true,
                    isSelected = true,
                    isInMonth = true,
                    schedules =
                        listOf(
                            AcademicScheduleUiModel(
                                "",
                                LocalDate.now(),
                                LocalDate.now(),
                            ),
                            AcademicScheduleUiModel(
                                "",
                                LocalDate.now(),
                                LocalDate.now(),
                            ),
                            PersonalScheduleUiModel(
                                0L,
                                "",
                                "",
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                "",
                            ),
                            PersonalScheduleUiModel(
                                0L,
                                "",
                                "",
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                "",
                            ),
                            PersonalScheduleUiModel(
                                0L,
                                "",
                                "",
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                "",
                            ),
                            PersonalScheduleUiModel(
                                0L,
                                "",
                                "",
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                "",
                            ),
                            PersonalScheduleUiModel(
                                0L,
                                "",
                                "",
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                "",
                            ),
                        ),
                ),
            onClickDate = { },
            modifier =
                Modifier
                    .width(60.dp)
                    .height(80.dp),
        )
    }
}
