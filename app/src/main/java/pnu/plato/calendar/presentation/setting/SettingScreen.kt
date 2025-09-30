package pnu.plato.calendar.presentation.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import pnu.plato.calendar.presentation.setting.intent.SettingEvent
import pnu.plato.calendar.presentation.setting.intent.SettingState

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                else -> Unit // TODO
            }
        }
    }

    SettingContent(
        state = state,
        lazyListState = lazyListState,
        onEvent = viewModel::setEvent,
        modifier = modifier,
    )
}

@Composable
fun SettingContent(
    state: SettingState,
    lazyListState: LazyListState,
    onEvent: (SettingEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = lazyListState,
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
                    text = "설정",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryColor,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingPreview() {
    PlatoCalendarTheme {
        SettingContent(
            state = SettingState(),
            lazyListState = rememberLazyListState(),
            onEvent = {},
        )
    }
}
