package pnu.plato.calendar.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import pnu.plato.calendar.presentation.common.component.TopBar
import pnu.plato.calendar.presentation.common.extension.noRippleClickable
import pnu.plato.calendar.presentation.common.theme.Gray
import pnu.plato.calendar.presentation.common.theme.MediumGray
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import pnu.plato.calendar.presentation.common.theme.White
import pnu.plato.calendar.presentation.setting.intent.SettingEvent
import pnu.plato.calendar.presentation.setting.intent.SettingEvent.NavigateToWebView
import pnu.plato.calendar.presentation.setting.intent.SettingSideEffect
import pnu.plato.calendar.presentation.setting.intent.SettingState
import pnu.plato.calendar.presentation.setting.model.NotificationTime

private const val LOGIN_REQUIRED = "로그인이 필요합니다."

@Composable
fun SettingScreen(
    navigateToWebView: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is SettingSideEffect.NavigateToWebView -> navigateToWebView(sideEffect.url)
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
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            TopBar(title = "설정")
        }

        item {
            SettingSection(title = "계정") {
                Account(
                    state = state,
                    onClickLoginLogout = {
                        val isLoggedIn = state.userInfo != null
                        onEvent(if (isLoggedIn) SettingEvent.Logout else SettingEvent.ShowLoginDialog)
                    },
                )
            }
        }

        item {
            SettingSection(title = "알림") {
                var isFirstReminderDropdownExpanded by remember { mutableStateOf(false) }
                var isSecondReminderDropdownExpanded by remember { mutableStateOf(false) }

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "알림 허용하기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(
                        checked = state.notificationsEnabled,
                        onCheckedChange = { enabled ->
                            onEvent(SettingEvent.SetNotificationsEnabled(enabled))
                        },
                        colors =
                            SwitchDefaults.colors(
                                checkedTrackColor = PrimaryColor,
                            ),
                    )
                }
                Spacer(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MediumGray),
                )

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "학사 일정 알림받기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(
                        checked = state.academicScheduleEnabled,
                        onCheckedChange = { enabled ->
                            onEvent(SettingEvent.SetAcademicScheduleEnabled(enabled))
                        },
                        colors =
                            SwitchDefaults.colors(
                                checkedTrackColor = PrimaryColor,
                            ),
                    )
                }

                Spacer(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MediumGray),
                )

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "알림",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )

                    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                        Row(
                            modifier =
                                Modifier.noRippleClickable {
                                    isFirstReminderDropdownExpanded = true
                                },
                        ) {
                            Text(
                                text = state.firstReminderTime.label,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Gray,
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MediumGray,
                            )
                        }

                        DropdownMenu(
                            expanded = isFirstReminderDropdownExpanded,
                            onDismissRequest = { isFirstReminderDropdownExpanded = false },
                        ) {
                            NotificationTime.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        onEvent(SettingEvent.SetFirstReminderTime(option))
                                        isFirstReminderDropdownExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MediumGray),
                )

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "두 번째 알림",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )

                    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                        Row(
                            modifier =
                                Modifier.noRippleClickable {
                                    isSecondReminderDropdownExpanded = true
                                },
                        ) {
                            Text(
                                text = state.secondReminderTime.label,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Gray,
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MediumGray,
                            )
                        }

                        DropdownMenu(
                            expanded = isSecondReminderDropdownExpanded,
                            onDismissRequest = { isSecondReminderDropdownExpanded = false },
                        ) {
                            NotificationTime.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        onEvent(SettingEvent.SetSecondReminderTime(option))
                                        isSecondReminderDropdownExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            SettingSection(title = "고객 지원") {
                SettingItem(text = "공지") { url -> onEvent(NavigateToWebView(url)) }

                Spacer(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MediumGray),
                )

                SettingItem(text = "문의하기") { _ -> }
            }
        }

        item {
            SettingSection(title = "이용 안내") {
                SettingItem(text = "서비스 이용약관") { _ -> }

                Spacer(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MediumGray),
                )

                SettingItem(text = "개인정보 처리방침") { _ -> }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Gray,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 8.dp),
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = White),
    ) {
        Column { content() }
    }
}

@Composable
private fun SettingItem(
    text: String,
    navigateToWebView: (String) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { navigateToWebView("https://glaze-mustang-7cf.notion.site/28057846cad680089524ea45cb9afce1?source=copy_link") }
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MediumGray,
        )
    }
}

@Composable
private fun Account(
    state: SettingState,
    onClickLoginLogout: () -> Unit,
) {
    val isLoggedIn = state.userInfo != null
    val userName = if (isLoggedIn) state.userInfo else LOGIN_REQUIRED
    val buttonText = if (isLoggedIn) "로그아웃" else "로그인"

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Account",
            tint = PrimaryColor,
            modifier = Modifier.size(28.dp),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = userName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier =
                Modifier
                    .width(76.dp)
                    .height(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryColor)
                    .clickable { onClickLoginLogout() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = buttonText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = White,
            )
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
