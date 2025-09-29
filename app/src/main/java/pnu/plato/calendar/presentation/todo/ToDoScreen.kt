package pnu.plato.calendar.presentation.todo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pnu.plato.calendar.presentation.common.extension.noRippleClickable
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import pnu.plato.calendar.presentation.common.theme.White
import pnu.plato.calendar.presentation.todo.component.ToDoScheduleItem
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent
import pnu.plato.calendar.presentation.todo.intent.ToDoState
import java.time.LocalDate
import java.time.LocalDateTime

private const val HAS_NO_SCHEDULE = "일정 없음"

@Composable
fun ToDoScreen(
    modifier: Modifier = Modifier,
    viewModel: ToDoViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                else -> Unit // TODO
            }
        }
    }

    ToDoContent(
        state = state,
        onEvent = viewModel::setEvent,
        modifier = modifier,
    )
}

@Composable
fun ToDoContent(
    state: ToDoState,
    onEvent: (ToDoEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val within7Days = state.within7Days
    val completedSchedules = state.completedSchedules
    val courseSchedules = state.courseSchedules
    val customSchedules = state.customSchedules
    val academicSchedules = state.academicSchedules

    LazyColumn(
        modifier =
            modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                modifier =
                    Modifier
                        .statusBarsPadding()
                        .padding(vertical = 12.dp)
                        .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "할일",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor,
                )
            }
        }

        item {
            ExpandableSection(
                title = "7일 이내",
                icon = Icons.Default.DateRange,
                items = within7Days,
                toggleCompletion = { id, completed ->
                    onEvent(ToDoEvent.TogglePersonalScheduleCompletion(id, completed))
                },
                initiallyExpanded = true,
            )
        }

        item {
            ExpandableSection(
                title = "완료",
                icon = Icons.Default.CheckCircle,
                items = completedSchedules,
                toggleCompletion = { id, completed ->
                    onEvent(ToDoEvent.TogglePersonalScheduleCompletion(id, completed))
                },
            )
        }

        item {
            ExpandableSection(
                title = "강의 일정",
                icon = Icons.Default.DateRange,
                items = courseSchedules,
                toggleCompletion = { id, completed ->
                    onEvent(ToDoEvent.TogglePersonalScheduleCompletion(id, completed))
                },
            )
        }

        item {
            ExpandableSection(
                title = "개인 일정",
                icon = Icons.Default.DateRange,
                items = customSchedules,
                toggleCompletion = { id, completed ->
                    onEvent(ToDoEvent.TogglePersonalScheduleCompletion(id, completed))
                },
            )
        }

        item {
            ExpandableSection(
                title = "학사 일정",
                icon = Icons.Default.DateRange,
                items = academicSchedules,
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ExpandableSection(
    title: String,
    icon: ImageVector,
    items: List<ScheduleUiModel>,
    toggleCompletion: (Long, Boolean) -> Unit = { _, _ -> },
    initiallyExpanded: Boolean = false,
) {
    var expanded by rememberSaveable { mutableStateOf(initiallyExpanded) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "rotation")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = White),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .noRippleClickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PrimaryColor)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
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

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (items.isEmpty()) {
                    Text(
                        text = HAS_NO_SCHEDULE,
                        modifier = Modifier.padding(start = 18.dp, bottom = 18.dp),
                        fontSize = 14.sp,
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
                                toggleCompletion = { id, isCompleted ->
                                    toggleCompletion(
                                        id,
                                        isCompleted,
                                    )
                                },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min)
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
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
fun ToDoScreenPreview() {
    PlatoCalendarTheme {
        ToDoContent(
            state =
                ToDoState(
                    listOf(
                        AcademicScheduleUiModel(
                            title = "신정",
                            startAt = LocalDate.of(2024, 1, 11),
                            endAt = LocalDate.now().plusDays(2),
                        ),
                        CustomScheduleUiModel(
                            id = 1L,
                            title = "새해 계획 세우기",
                            description = "",
                            startAt = LocalDateTime.of(2024, 1, 11, 14, 0),
                            endAt = LocalDateTime.now().plusDays(2).plusHours(3),
                            isCompleted = false,
                        ),
                        CourseScheduleUiModel(
                            id = 7592,
                            title = "과제1",
                            description = "",
                            startAt = LocalDateTime.now(),
                            endAt = LocalDateTime.now().plusDays(3).plusHours(7),
                            isCompleted = false,
                            courseName = "운영체제",
                        ),
                        CustomScheduleUiModel(
                            id = 2L,
                            title = "완료된 과제",
                            description = "",
                            startAt = LocalDateTime.now().minusDays(3),
                            endAt = LocalDateTime.now().minusDays(1),
                            isCompleted = true,
                        ),
                        CourseScheduleUiModel(
                            id = 7593,
                            title = "완료된 강의 과제",
                            description = "",
                            startAt = LocalDateTime.now().minusDays(5),
                            endAt = LocalDateTime.now().minusDays(2),
                            isCompleted = true,
                            courseName = "데이터베이스",
                        ),
                    ),
                ),
            onEvent = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
