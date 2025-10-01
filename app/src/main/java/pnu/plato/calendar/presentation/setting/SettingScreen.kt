package pnu.plato.calendar.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pnu.plato.calendar.presentation.common.component.LoginDialog
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import pnu.plato.calendar.presentation.common.theme.White
import pnu.plato.calendar.presentation.setting.intent.SettingEvent
import pnu.plato.calendar.presentation.setting.intent.SettingState

private const val LOGIN_REQUIRED = "로그인이 필요합니다."

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

    if (state.isLoginDialogVisible) {
        LoginDialog(
            onDismissRequest = { viewModel.setEvent(SettingEvent.HideLoginDialog) },
            onLoginRequest = { loginCredentials -> viewModel.tryLogin(loginCredentials) },
        )
    }
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

        item {
            Card(
                modifier = modifier,
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = White),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Account",
                        tint = PrimaryColor,
                        modifier = Modifier.size(28.dp),
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    val userName = state.userInfo ?: LOGIN_REQUIRED
                    Text(text = userName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier =
                            Modifier
                                .width(76.dp)
                                .height(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryColor)
                                .clickable {
                                    if (state.userInfo != null) {
                                        onEvent(SettingEvent.Logout)
                                    } else {
                                        onEvent(SettingEvent.ShowLoginDialog)
                                    }
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        val text = if (state.userInfo != null) "로그아웃" else "로그인"
                        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = White)
                    }
                }
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
