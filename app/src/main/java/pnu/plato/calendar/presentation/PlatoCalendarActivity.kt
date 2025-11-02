package pnu.plato.calendar.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pnu.plato.calendar.presentation.common.component.AnimatedToast
import pnu.plato.calendar.presentation.common.eventbus.NotificationEvent
import pnu.plato.calendar.presentation.common.eventbus.NotificationEventBus
import pnu.plato.calendar.presentation.common.extension.noRippleClickable
import pnu.plato.calendar.presentation.common.manager.LoginManager
import pnu.plato.calendar.presentation.common.manager.ScheduleManager
import pnu.plato.calendar.presentation.common.manager.SettingsManager
import pnu.plato.calendar.presentation.common.navigation.PlatoCalendarBottomBar
import pnu.plato.calendar.presentation.common.navigation.PlatoCalendarNavHost
import pnu.plato.calendar.presentation.common.notification.AlarmScheduler
import pnu.plato.calendar.presentation.common.notification.NotificationHelper
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import pnu.plato.calendar.presentation.common.theme.White
import javax.inject.Inject

@AndroidEntryPoint
class PlatoCalendarActivity : ComponentActivity() {
    @Inject
    lateinit var loginManager: LoginManager

    @Inject
    lateinit var scheduleManager: ScheduleManager

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                lifecycleScope.launch {
                    settingsManager.setNotificationsEnabled(isGranted)
                }
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        MobileAds.initialize(this)

        lifecycleScope.launch {
            loginManager.autoLogin()
            handleNotificationIntent(intent)
        }

        enableEdgeToEdge(
            statusBarStyle =
                SystemBarStyle.light(
                    scrim = Color.TRANSPARENT,
                    darkScrim = Color.TRANSPARENT,
                ),
            navigationBarStyle =
                SystemBarStyle.light(
                    scrim = Color.TRANSPARENT,
                    darkScrim = Color.TRANSPARENT,
                ),
        )

        setContent {
            val navController = rememberNavController()
            val isLoading by scheduleManager.isLoading.collectAsStateWithLifecycle()

            PlatoCalendarTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        bottomBar = {
                            PlatoCalendarBottomBar(navController = navController)
                        },
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        modifier = Modifier.fillMaxSize(),
                    ) { paddingValues ->
                        PlatoCalendarNavHost(
                            navController = navController,
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .background(White)
                                    .padding(paddingValues),
                        )
                    }

                    AnimatedToast()

                    if (isLoading) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .noRippleClickable(),
                        ) {
                            CircularProgressIndicator(
                                color = PrimaryColor,
                                modifier =
                                    Modifier
                                        .align(Alignment.Center)
                                        .navigationBarsPadding(),
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent) {
        val scheduleId = intent.getLongExtra(NotificationHelper.EXTRA_SCHEDULE_ID, -1L)
        if (scheduleId != -1L) {
            lifecycleScope.launch {
                NotificationEventBus.sendEvent(NotificationEvent.OpenSchedule(scheduleId))
            }
        }
    }
}
