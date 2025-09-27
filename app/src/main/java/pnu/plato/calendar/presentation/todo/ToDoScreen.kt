package pnu.plato.calendar.presentation.todo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent
import pnu.plato.calendar.presentation.todo.intent.ToDoState

@Composable
fun ToDoScreen(modifier: Modifier = Modifier, viewModel: ToDoViewModel = hiltViewModel()) {
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
        modifier = modifier
    )
}

@Composable
fun ToDoContent(state: ToDoState, onEvent: (ToDoEvent) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {

    }
}

@Preview(showBackground = true)
@Composable
fun ToDoScreenPreview() {
    PlatoCalendarTheme {
        ToDoContent(state = ToDoState(), onEvent = {}, modifier = Modifier.fillMaxSize())
    }
}