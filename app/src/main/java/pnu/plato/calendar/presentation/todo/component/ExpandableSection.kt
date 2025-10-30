package pnu.plato.calendar.presentation.todo.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.common.extension.noRippleClickable
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import pnu.plato.calendar.presentation.common.theme.VeryLightGray
import pnu.plato.calendar.presentation.todo.model.ToDoSection
import java.time.LocalDateTime

private const val HAS_NO_SCHEDULE = "일정 없음"

@Composable
fun ExpandableSection(
    toDoSection: ToDoSection,
    items: List<ScheduleUiModel>,
    today: LocalDateTime,
    isExpanded: Boolean,
    onSectionClick: (ToDoSection) -> Unit,
    toggleCompletion: (Long, Boolean) -> Unit,
    onScheduleClick: (ScheduleUiModel) -> Unit,
) {
    val rotation by animateFloatAsState(if (isExpanded) 180f else 0f, label = "rotation")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = VeryLightGray),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .noRippleClickable { onSectionClick(toDoSection) }
                    .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = toDoSection.icon, contentDescription = null, tint = PrimaryColor)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = toDoSection.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = PrimaryColor,
                modifier = Modifier.rotate(rotation),
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (items.isEmpty()) {
                    Text(
                        text = HAS_NO_SCHEDULE,
                        modifier = Modifier.padding(start = 18.dp, bottom = 18.dp, top = 6.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor,
                    )
                } else {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                    ) {
                        items.forEach { schedule ->
                            ToDoScheduleItem(
                                schedule = schedule,
                                today = today,
                                toggleCompletion = { id, isCompleted ->
                                    toggleCompletion(id, isCompleted)
                                },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min)
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                        .noRippleClickable { onScheduleClick(schedule) },
                            )
                        }
                    }
                }
            }
        }
    }
}
