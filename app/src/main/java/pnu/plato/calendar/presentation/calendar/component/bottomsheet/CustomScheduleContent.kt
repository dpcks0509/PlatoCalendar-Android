package pnu.plato.calendar.presentation.calendar.component.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pnu.plato.calendar.presentation.common.extension.noRippleClickable
import pnu.plato.calendar.presentation.common.theme.Black
import pnu.plato.calendar.presentation.common.theme.Gray
import pnu.plato.calendar.presentation.common.theme.LightGray
import pnu.plato.calendar.presentation.common.theme.Red
import pnu.plato.calendar.presentation.common.theme.White
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val HAS_NO_DESCRIPTION = "설명 없음"

@Composable
fun CustomScheduleContent(
    schedule: CustomScheduleUiModel,
    editSchedule: (CustomSchedule) -> Unit,
    deleteSchedule: (Long) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var title: String by remember { mutableStateOf(schedule.title) }
    var description: String by remember { mutableStateOf(schedule.description.orEmpty()) }
    var startAt: LocalDateTime by remember { mutableStateOf(schedule.startAt) }
    var endAt: LocalDateTime by remember { mutableStateOf(schedule.endAt) }

    val dateFormatter = DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN)
    val timeFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)
    val formattedStartDate = remember(startAt) { startAt.format(dateFormatter) }
    val formattedStartTime = remember(startAt) { startAt.format(timeFormatter) }
    val formattedEndDate = remember(endAt) { endAt.format(dateFormatter) }
    val formattedEndTime = remember(endAt) { endAt.format(timeFormatter) }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(schedule.color)
                .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "Back",
            tint = White,
            modifier =
                Modifier
                    .size(32.dp)
                    .noRippleClickable(onDismissRequest),
        )

        ActionButton(
            text = "수정",
            onClick = {
                editSchedule(
                    CustomSchedule(
                        id = schedule.id,
                        title = title,
                        description = description,
                        startAt = startAt,
                        endAt = endAt,
                    ),
                )
            },
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier =
            Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), clip = true)
                .background(White),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier =
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(schedule.color),
        )

        TextField(
            value = title,
            onValueChange = { title = it },
            textStyle =
                TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Black,
                ),
            modifier = Modifier.fillMaxWidth(),
            colors =
                TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    cursorColor = schedule.color,
                ),
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Column(
        modifier =
            Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), clip = true)
                .background(White)
                .padding(vertical = 18.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Description",
                tint = Black,
                modifier = Modifier.size(24.dp),
            )

            Spacer(modifier = Modifier.width(8.dp))

            if (schedule.description.isNullOrEmpty()) {
                Text(
                    text = HAS_NO_DESCRIPTION,
                    fontSize = 16.sp,
                    color = Gray,
                )
            } else {
                Text(
                    text = schedule.description,
                    fontSize = 16.sp,
                    color = Black,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Description",
                tint = Black,
                modifier = Modifier.size(24.dp),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightGray)
                        .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(text = "시작", fontSize = 14.sp, color = Gray)
                Text(text = formattedStartDate, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Black)
                Text(text = formattedStartTime, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Black)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Arrow",
                tint = Black,
                modifier = Modifier.size(24.dp),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightGray)
                        .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(text = "종료", fontSize = 14.sp, color = Gray)
                Text(text = formattedEndDate, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Black)
                Text(text = formattedEndTime, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Black)
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "일정 삭제",
        fontSize = 14.sp,
        color = Red,
        textAlign = TextAlign.Center,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(36.dp)
                .noRippleClickable { deleteSchedule(schedule.id) },
    )

    Spacer(modifier = Modifier.height(12.dp))
}
