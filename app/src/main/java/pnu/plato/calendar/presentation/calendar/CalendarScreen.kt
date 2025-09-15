package pnu.plato.calendar.presentation.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import pnu.plato.calendar.presentation.calendar.component.CalendarTopBar
import pnu.plato.calendar.presentation.calendar.intent.CalendarEvent
import pnu.plato.calendar.presentation.calendar.intent.CalendarState
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme

@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        viewModel.setEvent(CalendarEvent.FetchPersonalSchedules)
    }

    CalendarContent(
        state = state,
        onEvent = viewModel::setEvent,
    )
}

@Composable
fun CalendarContent(
    state: CalendarState,
    onEvent: (CalendarEvent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CalendarTopBar(
            state = state,
            onEvent = onEvent,
            showMakePersonalScheduleBottomSheet = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    PlatoCalendarTheme {
        CalendarContent(
            state = CalendarState(),
            onEvent = {},
        )
    }
}
