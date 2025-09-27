package pnu.plato.calendar.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pnu.plato.calendar.BuildConfig.BANNER_AD_UNIT_ID
import pnu.plato.calendar.presentation.PlatoCalendarActivity.Companion.today
import pnu.plato.calendar.presentation.calendar.component.Calendar
import pnu.plato.calendar.presentation.calendar.component.CalendarTopBar
import pnu.plato.calendar.presentation.calendar.component.MAX_MONTH_SIZE
import pnu.plato.calendar.presentation.calendar.component.SelectedDateScheduleInfo
import pnu.plato.calendar.presentation.calendar.component.bottomsheet.ScheduleBottomSheet
import pnu.plato.calendar.presentation.calendar.component.bottomsheet.ScheduleBottomSheetContent
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.DeleteCustomSchedule
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.EditCustomSchedule
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.HideScheduleBottomSheet
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MakeCustomSchedule
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MoveToToday
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.ShowScheduleBottomSheet
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.TogglePersonalScheduleCompletion
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.UpdateCurrentYearMonth
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.UpdateSchedules
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.UpdateSelectedDate
import pnu.plato.calendar.presentation.calendar.intent.CalendarSideEffect
import pnu.plato.calendar.presentation.calendar.intent.CalendarState
import pnu.plato.calendar.presentation.calendar.model.DaySchedule
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.extension.noRippleClickable
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState =
        rememberPagerState(
            initialPage = 0,
            pageCount = { if (today.dayOfMonth != 1) MAX_MONTH_SIZE else MAX_MONTH_SIZE - 1 },
        )
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val adView =
        remember {
            AdView(context).apply {
                adUnitId = BANNER_AD_UNIT_ID
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
    val adRequest = AdRequest.Builder().build()

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                CalendarSideEffect.HideScheduleBottomSheet -> coroutineScope.launch { sheetState.hide() }
            }
        }
    }

    LaunchedEffect(state.schedules) {
        viewModel.setEvent(UpdateSchedules)
    }

    LaunchedEffect(sheetState.currentValue) {
        if (sheetState.currentValue == SheetValue.Hidden) {
            viewModel.setEvent(HideScheduleBottomSheet)
            adView.loadAd(adRequest)
        }
    }

    DisposableEffect(Unit) {
        onDispose { adView.destroy() }
    }

    CalendarContent(
        state = state,
        adView = adView,
        pagerState = pagerState,
        sheetState = sheetState,
        coroutineScope = coroutineScope,
        getMonthSchedule = viewModel::getMonthSchedule,
        onEvent = viewModel::setEvent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarContent(
    state: CalendarState,
    adView: AdView,
    pagerState: PagerState,
    sheetState: SheetState,
    coroutineScope: CoroutineScope,
    getMonthSchedule: (YearMonth) -> List<SnapshotStateList<DaySchedule?>>,
    onEvent: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        CalendarTopBar(
            selectedDate = state.selectedDate,
            currentYearMonth = state.currentYearMonth,
            moveToToday = {
                onEvent(MoveToToday)
                coroutineScope.launch { pagerState.scrollToPage(0) }
            },
            onMakeScheduleClick = { onEvent(ShowScheduleBottomSheet()) },
            modifier =
                Modifier
                    .background(PrimaryColor)
                    .statusBarsPadding()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(50.dp),
        )

        Calendar(
            pagerState = pagerState,
            getMonthSchedule = getMonthSchedule,
            onDateClick = { date -> onEvent(UpdateSelectedDate(date)) },
            onMonthSwipe = { yearMonth -> onEvent(UpdateCurrentYearMonth(yearMonth)) },
            modifier = Modifier.fillMaxWidth(),
        )

        SelectedDateScheduleInfo(
            selectedDate = state.selectedDate,
            schedules = state.selectedDateSchedules,
            onScheduleClick = { schedule -> onEvent(ShowScheduleBottomSheet(schedule)) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 8.dp, start = 12.dp, end = 8.dp),
        )
    }

    if (state.isLoading) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .noRippleClickable(),
        ) {
            CircularProgressIndicator(
                color = PrimaryColor,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }

    if (state.isScheduleBottomSheetVisible) {
        ScheduleBottomSheet(
            content = state.scheduleBottomSheetContent,
            adView = adView,
            sheetState = sheetState,
            makeSchedule = { schedule -> onEvent(MakeCustomSchedule(schedule)) },
            editSchedule = { schedule -> onEvent(EditCustomSchedule(schedule)) },
            deleteSchedule = { id -> onEvent(DeleteCustomSchedule(id)) },
            toggleScheduleCompletion = { id, isCompleted ->
                onEvent(
                    TogglePersonalScheduleCompletion(
                        id,
                        isCompleted
                    )
                )
            },
            onDismissRequest = { coroutineScope.launch { sheetState.hide() } },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    PlatoCalendarTheme {
        val base = LocalDate.of(2024, 1, 1)
        val schedules =
            listOf(
                AcademicScheduleUiModel(
                    title = "신정",
                    startAt = LocalDate.of(2024, 1, 11),
                    endAt = LocalDate.of(2024, 1, 11),
                ),
                CustomScheduleUiModel(
                    id = 1L,
                    title = "새해 계획 세우기",
                    description = "",
                    startAt = LocalDateTime.of(2024, 1, 11, 14, 0),
                    endAt = LocalDateTime.of(2024, 1, 11, 16, 0),
                    isCompleted = false,
                ),
            )

        val monthSchedule =
            List(6) { week ->
                List<DaySchedule?>(7) { day ->
                    val date = base.minusDays(1).plusDays((week * 7 + day).toLong())
                    DaySchedule(
                        date = date,
                        isToday = date.dayOfMonth == 8,
                        isSelected = date.dayOfMonth == 11,
                        isInMonth = date.monthValue == 1,
                        schedules = schedules,
                    )
                }.toMutableStateList()
            }

        val context = LocalContext.current

        CalendarContent(
            state =
                CalendarState(
                    selectedDate = LocalDate.of(2024, 1, 11),
                    schedules = schedules,
                    isScheduleBottomSheetVisible = false,
                    scheduleBottomSheetContent = ScheduleBottomSheetContent.NewScheduleContent,
                ),
            adView = remember { AdView(context) },
            pagerState = rememberPagerState(initialPage = 0, pageCount = { 12 }),
            sheetState = rememberModalBottomSheetState(),
            coroutineScope = rememberCoroutineScope(),
            getMonthSchedule = { yearMonth -> monthSchedule },
            onEvent = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
