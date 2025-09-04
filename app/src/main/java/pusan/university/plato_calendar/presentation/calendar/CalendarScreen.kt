package pusan.university.plato_calendar.presentation.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarEvent
import pusan.university.plato_calendar.presentation.calendar.intent.CalendarState
import pusan.university.plato_calendar.presentation.common.theme.PlatoCalendarTheme

@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val loginStatus by viewModel.loginManager.loginStatus.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->

        }
    }

    LaunchedEffect(loginStatus) {
        viewModel.setEvent(CalendarEvent.FetchSchedules)
    }

    CalendarContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}

@Composable
fun CalendarContent(
    state: CalendarState,
    onEvent: (CalendarEvent) -> Unit
) {

}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    PlatoCalendarTheme {
        CalendarContent(
            state = CalendarState(),
            onEvent = {}
        )
    }
}