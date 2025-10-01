package pnu.plato.calendar.presentation.todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import kotlinx.coroutines.launch
import pnu.plato.calendar.BuildConfig
import pnu.plato.calendar.presentation.PlatoCalendarActivity.Companion.today
import pnu.plato.calendar.presentation.calendar.component.bottomsheet.ScheduleBottomSheet
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CourseScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pnu.plato.calendar.presentation.common.component.TopBar
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import pnu.plato.calendar.presentation.todo.component.ExpandableSection
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent.DeleteCustomSchedule
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent.EditCustomSchedule
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent.HideScheduleBottomSheet
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent.ShowScheduleBottomSheet
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent.TogglePersonalScheduleCompletion
import pnu.plato.calendar.presentation.todo.intent.ToDoSideEffect
import pnu.plato.calendar.presentation.todo.intent.ToDoState
import pnu.plato.calendar.presentation.todo.model.ToDoSection
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoScreen(
    modifier: Modifier = Modifier,
    viewModel: ToDoViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val adView =
        remember {
            AdView(context).apply {
                adUnitId = BuildConfig.BANNER_AD_UNIT_ID
                val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, 360)
                setAdSize(adSize)

                adListener =
                    object : AdListener() {
                        override fun onAdLoaded() {}

                        override fun onAdFailedToLoad(error: LoadAdError) {}

                        override fun onAdImpression() {}

                        override fun onAdClicked() {}
                    }
            }
        }
    val adRequest = remember { AdRequest.Builder().build() }

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                ToDoSideEffect.HideScheduleBottomSheet -> coroutineScope.launch { sheetState.hide() }
                else -> Unit
            }
        }
    }

    LaunchedEffect(sheetState.currentValue) {
        if (sheetState.currentValue == SheetValue.Hidden) {
            viewModel.setEvent(HideScheduleBottomSheet)
            adView.loadAd(adRequest)
        }
    }

    ToDoContent(
        state = state,
        onEvent = viewModel::setEvent,
        modifier = modifier,
    )

    if (state.isScheduleBottomSheetVisible) {
        ScheduleBottomSheet(
            content = state.scheduleBottomSheetContent,
            selectedDate = today,
            adView = adView,
            sheetState = sheetState,
            makeSchedule = { Unit },
            editSchedule = { schedule -> viewModel.setEvent(EditCustomSchedule(schedule)) },
            deleteSchedule = { id -> viewModel.setEvent(DeleteCustomSchedule(id)) },
            toggleScheduleCompletion = { id, completed ->
                viewModel.setEvent(TogglePersonalScheduleCompletion(id, completed))
            },
            onDismissRequest = { coroutineScope.launch { sheetState.hide() } },
            modifier = Modifier.fillMaxWidth(),
        )
    }
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

    var expandedToDoSection by rememberSaveable { mutableStateOf<ToDoSection?>(ToDoSection.WITHIN_7_DAYS) }
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier =
            modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            TopBar(title = "할일")
        }

        items(ToDoSection.entries.toList()) { section ->
            val schedules =
                when (section) {
                    ToDoSection.WITHIN_7_DAYS -> within7Days
                    ToDoSection.COMPLETED -> completedSchedules
                    ToDoSection.COURSE -> courseSchedules
                    ToDoSection.CUSTOM -> customSchedules
                    ToDoSection.ACADEMIC -> academicSchedules
                }

            ExpandableSection(
                toDoSection = section,
                items = schedules,
                isExpanded = expandedToDoSection == section,
                onSectionClick = { clickedSection ->
                    expandedToDoSection =
                        if (expandedToDoSection == clickedSection) {
                            null
                        } else {
                            coroutineScope.launch { lazyListState.scrollToItem(0) }
                            clickedSection
                        }
                },
                toggleCompletion = { id, completed ->
                    onEvent(TogglePersonalScheduleCompletion(id, completed))
                },
                onScheduleClick = { schedule -> onEvent(ShowScheduleBottomSheet(schedule)) },
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
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
