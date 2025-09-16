package pnu.plato.calendar.presentation.calendar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
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
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun DayItem(
    day: DayUiModel,
    onClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .then(
                    if (day.isSelected) {
                        Modifier
                            .clip(
                                RoundedCornerShape(12.dp),
                            ).background(LightGray)
                    } else {
                        Modifier.background(Color.White)
                    },
                ).clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { onClick(day.date) },
                ).padding(top = 6.dp, bottom = 12.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if (day.isToday) {
            Box(
                modifier =
                    Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(PrimaryColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        } else {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            items(day.schedules) { schedule ->
                Box(
                    modifier =
                        Modifier
                            .size(6.dp)
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
                    schedules =
                        listOf(
                            ScheduleUiModel.AcademicScheduleUiModel("", LocalDate.now(), LocalDate.now()),
                            ScheduleUiModel.AcademicScheduleUiModel("", LocalDate.now(), LocalDate.now()),
                            ScheduleUiModel.PersonalScheduleUiModel(0L, "", "", LocalDateTime.now(), LocalDateTime.now(), ""),
                            ScheduleUiModel.PersonalScheduleUiModel(0L, "", "", LocalDateTime.now(), LocalDateTime.now(), ""),
                            ScheduleUiModel.PersonalScheduleUiModel(0L, "", "", LocalDateTime.now(), LocalDateTime.now(), ""),
                        ),
                ),
            onClick = { },
            modifier =
                Modifier
                    .width(60.dp)
                    .height(80.dp),
        )
    }
}
