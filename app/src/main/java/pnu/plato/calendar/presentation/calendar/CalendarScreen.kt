package pnu.plato.calendar.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import pnu.plato.calendar.presentation.calendar.component.Calendar
import pnu.plato.calendar.presentation.calendar.component.CalendarTopBar
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.ChangeCurrentYearMonth
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.ChangeSelectedDate
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent.MoveToToday
import pnu.plato.calendar.presentation.calendar.intent.CalendarState
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.common.theme.PrimaryColor

@Composable
fun CalendarScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        viewModel.setEvent(CalendarEvent.GetPersonalSchedules)
    }

    CalendarContent(
        state = state,
        onEvent = viewModel::setEvent,
        modifier = modifier,
    )
}

@Composable
fun CalendarContent(
    state: CalendarState,
    onEvent: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        CalendarTopBar(
            today = state.today,
            selectedDate = state.selectedDate,
            currentYearMonth = state.currentYearMonth,
            moveToToday = { onEvent(MoveToToday) },
            showMakePersonalScheduleBottomSheet = {},
            modifier =
                Modifier
                    .background(PrimaryColor)
                    .statusBarsPadding()
                    .padding(all = 16.dp)
                    .fillMaxWidth()
                    .height(50.dp),
        )

        Calendar(
            today = state.today,
            selectedDate = state.selectedDate,
            currentYearMonth = state.currentYearMonth,
            schedules = state.schedules,
            onClickDate = { date -> onEvent(ChangeSelectedDate(date)) },
            onSwipeMonth = { yearMonth -> onEvent(ChangeCurrentYearMonth(yearMonth)) },
            modifier = Modifier.fillMaxWidth(),
        )
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    PlatoCalendarTheme {
        CalendarContent(
            state = CalendarState(),
            onEvent = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
