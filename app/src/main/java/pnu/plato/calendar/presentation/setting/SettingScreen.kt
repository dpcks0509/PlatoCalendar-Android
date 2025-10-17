package pnu.plato.calendar.presentation.setting

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pnu.plato.calendar.presentation.common.component.LoginDialog
import pnu.plato.calendar.presentation.common.component.TopBar
import pnu.plato.calendar.presentation.common.theme.MediumGray
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.setting.component.Account
import pnu.plato.calendar.presentation.setting.component.NotificationPermissionSettingsDialog
import pnu.plato.calendar.presentation.setting.component.NotificationToggleItem
import pnu.plato.calendar.presentation.setting.component.ReminderDropdownItem
import pnu.plato.calendar.presentation.setting.component.SettingItem
import pnu.plato.calendar.presentation.setting.component.SettingSection
import pnu.plato.calendar.presentation.setting.intent.SettingEvent
import pnu.plato.calendar.presentation.setting.intent.SettingEvent.NavigateToWebView
import pnu.plato.calendar.presentation.setting.intent.SettingEvent.UpdateFirstReminderTime
import pnu.plato.calendar.presentation.setting.intent.SettingEvent.UpdateNotificationPermission
import pnu.plato.calendar.presentation.setting.intent.SettingEvent.UpdateNotificationsEnabled
import pnu.plato.calendar.presentation.setting.intent.SettingEvent.UpdateSecondReminderTime
import pnu.plato.calendar.presentation.setting.intent.SettingSideEffect
import pnu.plato.calendar.presentation.setting.intent.SettingState
import pnu.plato.calendar.presentation.setting.model.SettingMenu
import pnu.plato.calendar.presentation.setting.model.SettingMenu.ACCOUNT
import pnu.plato.calendar.presentation.setting.model.SettingMenu.NOTIFICATIONS
import pnu.plato.calendar.presentation.setting.model.SettingMenu.SettingContent.FIRST_REMINDER
import pnu.plato.calendar.presentation.setting.model.SettingMenu.SettingContent.NOTIFICATIONS_ENABLED
import pnu.plato.calendar.presentation.setting.model.SettingMenu.SettingContent.SECOND_REMINDER
import pnu.plato.calendar.presentation.setting.model.SettingMenu.USAGE_GUIDE
import pnu.plato.calendar.presentation.setting.model.SettingMenu.USER_SUPPORT

@Composable
fun SettingScreen(
    navigateToWebView: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current
    val activity = context as? Activity
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when {
                isGranted -> viewModel.setEvent(UpdateNotificationsEnabled(true))
                activity?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) == false -> {
                    viewModel.setEvent(SettingEvent.ShowNotificationPermissionSettingsDialog)
                }

                else -> viewModel.setEvent(UpdateNotificationsEnabled(false))
            }
        }

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        val granted = checkNotificationPermission(context)
        viewModel.setEvent(UpdateNotificationPermission(granted))
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is SettingSideEffect.NavigateToWebView -> navigateToWebView(sideEffect.url)
            }
        }
    }

    val handleSettingEvent: (SettingEvent) -> Unit = { event ->
        when (event) {
            is UpdateNotificationsEnabled -> {
                if (!event.enabled) {
                    viewModel.setEvent(event)
                } else {
                    if (checkNotificationPermission(context)) {
                        viewModel.setEvent(event)
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.setEvent(event)
                        }
                    }
                }
            }

            else -> viewModel.setEvent(event)
        }
    }


    SettingContent(
        state = state,
        lazyListState = lazyListState,
        onEvent = handleSettingEvent,
        modifier = modifier,
    )

    if (state.isLoginDialogVisible) {
        LoginDialog(
            onDismissRequest = { viewModel.setEvent(SettingEvent.HideLoginDialog) },
            onLoginRequest = { loginCredentials -> viewModel.tryLogin(loginCredentials) },
        )
    }

    if (state.isNotificationPermissionSettingsDialogVisible) {
        NotificationPermissionSettingsDialog(
            onDismissRequest = { viewModel.setEvent(SettingEvent.HideNotificationPermissionSettingsDialog) },
            onNavigateToSettings = {
                viewModel.setEvent(SettingEvent.HideNotificationPermissionSettingsDialog)
                val intent =
                    Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                context.startActivity(intent)
            },
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
                        ACCOUNT -> {
                            Account(
                                state = state,
                                onClickLoginLogout = {
                                    val isLoggedIn = state.userInfo != null
                                    onEvent(if (isLoggedIn) SettingEvent.Logout else SettingEvent.ShowLoginDialog)
                                },
                            )
                        }

                        NOTIFICATIONS -> {
                            menu.items.forEachIndexed { index, content ->
                                when {
                                    content == NOTIFICATIONS_ENABLED -> {
                                        NotificationToggleItem(
                                            label = content.getLabel(),
                                            checked = state.notificationsEnabled,
                                            onCheckedChange = { enabled ->
                                                onEvent(
                                                    UpdateNotificationsEnabled(enabled)
                                                )
                                            },
                                        )
                                    }

                                    content == FIRST_REMINDER -> {
                                        ReminderDropdownItem(
                                            label = content.getLabel(),
                                            selectedLabel = state.firstReminderTime.label,
                                            enabled = state.hasNotificationPermission,
                                            onSelect = { option ->
                                                onEvent(
                                                    UpdateFirstReminderTime(
                                                        option
                                                    )
                                                )
                                            },
                                        )
                                    }

                                    content == SECOND_REMINDER -> {
                                        ReminderDropdownItem(
                                            label = content.getLabel(),
                                            selectedLabel = state.secondReminderTime.label,
                                            onSelect = { option ->
                                                onEvent(
                                                    UpdateSecondReminderTime(
                                                        option
                                                    )
                                                )
                                            },
                                            enabled = state.hasNotificationPermission,
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

                        USER_SUPPORT, USAGE_GUIDE -> {
                            menu.items.forEachIndexed { index, content ->
                                SettingItem(
                                    content = content,
                                    navigateToWebView = { navigateUrl ->
                                        onEvent(
                                            NavigateToWebView(
                                                navigateUrl
                                            )
                                        )
                                    },
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

private fun checkNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
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
