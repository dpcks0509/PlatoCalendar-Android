package pnu.plato.calendar.presentation.calendar.component.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pnu.plato.calendar.domain.entity.Schedule.PersonalSchedule.CustomSchedule
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pnu.plato.calendar.presentation.common.extension.noRippleClickable
import pnu.plato.calendar.presentation.common.theme.Black
import pnu.plato.calendar.presentation.common.theme.Gray
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import pnu.plato.calendar.presentation.common.theme.White
import java.time.LocalDateTime

@Composable
fun CourseScheduleContent(
    schedule: CourseScheduleUiModel,
    editSchedule: (CustomSchedule) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var title: String by remember { mutableStateOf(schedule.title) }
    var description: String by remember { mutableStateOf(schedule.description.orEmpty()) }
    var startAt: LocalDateTime by remember { mutableStateOf(schedule.startAt) }
    var endAt: LocalDateTime by remember { mutableStateOf(schedule.endAt) }
    var isCompleted: Boolean by remember { mutableStateOf(schedule.isCompleted) }

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
                if (title.isNotEmpty()) {
                    editSchedule(
                        CustomSchedule(
                            id = schedule.id,
                            title = title,
                            description = description,
                            startAt = startAt,
                            endAt = endAt,
                            isCompleted = isCompleted,
                        ),
                    )
                }
            },
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Box(
        modifier =
            Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .height(80.dp)
                .border(width = 1.dp, color = Black),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "광고 공간", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Black)
    }

    Spacer(modifier = Modifier.height(12.dp))

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(36.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (schedule.isCompleted) "완료 해제" else "완료하기",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (schedule.isCompleted) Gray else PrimaryColor,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .noRippleClickable {
                        editSchedule(
                            CustomSchedule(
                                id = schedule.id,
                                title = schedule.title,
                                description = schedule.description,
                                startAt = schedule.startAt,
                                endAt = schedule.endAt,
                                isCompleted = !schedule.isCompleted,
                            ),
                        )
                    },
        )
    }

    Spacer(modifier = Modifier.height(18.dp))
}
