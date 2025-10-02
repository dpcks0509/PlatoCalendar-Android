package pnu.plato.calendar.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pnu.plato.calendar.presentation.common.component.LoginDialog
import pnu.plato.calendar.presentation.common.component.TopBar
import pnu.plato.calendar.presentation.common.theme.MediumGray
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.setting.component.Account
import pnu.plato.calendar.presentation.setting.component.NotificationToggleItem
import pnu.plato.calendar.presentation.setting.component.ReminderDropdownItem
import pnu.plato.calendar.presentation.setting.component.SettingItem
import pnu.plato.calendar.presentation.setting.component.SettingSection
import pnu.plato.calendar.presentation.setting.intent.SettingEvent
import pnu.plato.calendar.presentation.setting.intent.SettingEvent.NavigateToWebView
import pnu.plato.calendar.presentation.setting.intent.SettingSideEffect
import pnu.plato.calendar.presentation.setting.intent.SettingState
import pnu.plato.calendar.presentation.setting.model.SettingMenu

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

        SettingMenu.entries.forEach { menu ->
            item {
                SettingSection(title = menu.title) {
                    when (menu) {
                        SettingMenu.ACCOUNT -> {
                            Account(
                                state = state,
                                onClickLoginLogout = {
                                    val isLoggedIn = state.userInfo != null
                                    onEvent(if (isLoggedIn) SettingEvent.Logout else SettingEvent.ShowLoginDialog)
                                },
                            )
                        }

                        SettingMenu.NOTIFICATIONS -> {
                            menu.items.forEachIndexed { index, content ->
                                when {
                                    content == SettingMenu.SettingContent.NOTIFICATIONS_ENABLED -> {
                                        NotificationToggleItem(
                                            label = content.getLabel(),
                                            checked = state.notificationsEnabled,
                                            onCheckedChange = { enabled -> onEvent(SettingEvent.SetNotificationsEnabled(enabled)) },
                                        )
                                    }
                                    content == SettingMenu.SettingContent.ACADEMIC_SCHEDULE_ENABLED -> {
                                        NotificationToggleItem(
                                            label = content.getLabel(),
                                            checked = state.academicScheduleEnabled,
                                            onCheckedChange = { enabled -> onEvent(SettingEvent.SetAcademicScheduleEnabled(enabled)) },
                                        )
                                    }
                                    content == SettingMenu.SettingContent.FIRST_REMINDER -> {
                                        ReminderDropdownItem(
                                            label = content.getLabel(),
                                            selectedLabel = state.firstReminderTime.label,
                                            onSelect = { option -> onEvent(SettingEvent.SetFirstReminderTime(option)) },
                                        )
                                    }
                                    content == SettingMenu.SettingContent.SECOND_REMINDER -> {
                                        ReminderDropdownItem(
                                            label = content.getLabel(),
                                            selectedLabel = state.secondReminderTime.label,
                                            onSelect = { option -> onEvent(SettingEvent.SetSecondReminderTime(option)) },
                                        )
                                    }
                                    index != menu.items.lastIndex -> {
                                        Spacer(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(1.dp)
                                                    .background(MediumGray),
                                        )
                                    }
                                }
                            }
                        }

                        SettingMenu.USER_SUPPORT, SettingMenu.USAGE_GUIDE -> {
                            menu.items.forEachIndexed { index, content ->
                                SettingItem(
                                    content = content,
                                    navigateToWebView = { navigateUrl -> onEvent(NavigateToWebView(navigateUrl)) },
                                )

                                if (index != menu.items.lastIndex) {
                                    Spacer(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(MediumGray),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
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
