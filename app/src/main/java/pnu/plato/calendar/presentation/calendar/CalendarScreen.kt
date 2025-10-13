package pnu.plato.calendar.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
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
import kotlinx.coroutines.launch
import pnu.plato.calendar.BuildConfig.BANNER_AD_UNIT_ID
import pnu.plato.calendar.presentation.calendar.component.Calendar
import pnu.plato.calendar.presentation.calendar.component.CalendarTopBar
import pnu.plato.calendar.presentation.calendar.component.MAX_MONTH_SIZE
import pnu.plato.calendar.presentation.calendar.component.SelectedDateScheduleInfo
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.DeleteCustomSchedule
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.EditCustomSchedule
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.HideLoginDialog
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.HideScheduleBottomSheet
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MakeCustomSchedule
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MoveToToday
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.ShowScheduleBottomSheet
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.TogglePersonalScheduleCompletion
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.UpdateCurrentYearMonth
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.UpdateSelectedDate
import pnu.plato.calendar.presentation.calendar.intent.CalendarSideEffect
import pnu.plato.calendar.presentation.calendar.intent.CalendarState
import pnu.plato.calendar.presentation.calendar.model.DaySchedule
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.AcademicScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.ScheduleUiModel.PersonalScheduleUiModel.CustomScheduleUiModel
import pnu.plato.calendar.presentation.calendar.model.YearMonth
import pnu.plato.calendar.presentation.common.component.LoginDialog
import pnu.plato.calendar.presentation.common.component.PullToRefreshContainer
import pnu.plato.calendar.presentation.common.component.bottomsheet.ScheduleBottomSheet
import pnu.plato.calendar.presentation.common.component.bottomsheet.ScheduleBottomSheetContent
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
            pageCount = { if (state.today.dayOfMonth != 1) MAX_MONTH_SIZE else MAX_MONTH_SIZE - 1 },
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
                is CalendarSideEffect.ScrollToPage -> coroutineScope.launch {
                    pagerState.scrollToPage(
                        sideEffect.page
                    )
                }
            }
        }
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
        pagerState = pagerState,
        getMonthSchedule = viewModel::getMonthSchedule,
        onEvent = viewModel::setEvent,
        modifier = modifier,
    )

    if (state.isScheduleBottomSheetVisible) {
        ScheduleBottomSheet(
            content = state.scheduleBottomSheetContent,
            selectedDate = state.selectedDate,
            adView = adView,
            sheetState = sheetState,
            makeSchedule = { schedule -> viewModel.setEvent(MakeCustomSchedule(schedule)) },
            editSchedule = { schedule -> viewModel.setEvent(EditCustomSchedule(schedule)) },
            deleteSchedule = { id -> viewModel.setEvent(DeleteCustomSchedule(id)) },
            toggleScheduleCompletion = { id, isCompleted ->
                viewModel.setEvent(
                    TogglePersonalScheduleCompletion(
                        id,
                        isCompleted,
                    ),
                )
            },
            onDismissRequest = { coroutineScope.launch { sheetState.hide() } },
            modifier = Modifier.fillMaxWidth(),
        )
    }

    if (state.isLoginDialogVisible) {
        LoginDialog(
            onDismissRequest = { viewModel.setEvent(HideLoginDialog) },
            onLoginRequest = { loginCredentials -> viewModel.tryLogin(loginCredentials) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarContent(
    state: CalendarState,
    pagerState: PagerState,
    getMonthSchedule: (YearMonth) -> List<List<DaySchedule?>>,
    onEvent: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    PullToRefreshContainer(
        modifier = modifier,
        onRefresh = { onEvent(CalendarEvent.Refresh) },
    ) {
        Column(
            Modifier.verticalScroll(scrollState),
        ) {
            CalendarTopBar(
                selectedDate = state.selectedDate,
                currentYearMonth = state.currentYearMonth,
                todayDate = state.today,
                moveToToday = {
                    onEvent(MoveToToday)
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
                todayDate = state.today,
                baseTodayDate = state.baseToday,
                getMonthSchedule = getMonthSchedule,
                onDateClick = { date -> onEvent(UpdateSelectedDate(date)) },
                onMonthSwipe = { yearMonth -> onEvent(UpdateCurrentYearMonth(yearMonth)) },
                modifier = Modifier.fillMaxWidth(),
            )

            SelectedDateScheduleInfo(
                selectedDate = state.selectedDate,
                schedules = state.selectedDateSchedules,
                todayDate = state.today,
                onScheduleClick = { schedule -> onEvent(ShowScheduleBottomSheet(schedule)) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp, start = 12.dp, end = 8.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    PlatoCalendarTheme {
        val base = LocalDate.of(2024, 1, 1)
        val today = LocalDate.of(2024, 1, 11)
        val baseToday = LocalDate.of(2024, 1, 1)
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

        CalendarContent(
            state =
                CalendarState(
                    today = today,
                    selectedDate = LocalDate.of(2024, 1, 11),
                    baseToday = baseToday,
                    schedules = schedules,
                    isScheduleBottomSheetVisible = false,
                    scheduleBottomSheetContent = ScheduleBottomSheetContent.NewScheduleContent,
                ),
            pagerState = rememberPagerState(initialPage = 0, pageCount = { 12 }),
            getMonthSchedule = { yearMonth -> monthSchedule },
            onEvent = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
