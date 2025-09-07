package pnu.plato.calendar.presentation

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pnu.plato.calendar.presentation.common.eventbus.ErrorEventBus
import pnu.plato.calendar.presentation.common.manager.LoginManager
import pnu.plato.calendar.presentation.common.navigation.PlatoCalendarNavHost
import pnu.plato.calendar.presentation.common.theme.PlatoCalendarTheme
import javax.inject.Inject

@AndroidEntryPoint
class PlatoCalendarActivity : ComponentActivity() {
    @Inject
    lateinit var loginManager: LoginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            loginManager.autoLogin()
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            LaunchedEffect(Unit) {
                ErrorEventBus.errorMessage.collect {
                    // TODO - Show Error Toast
                }
            }

            PlatoCalendarTheme {
                PlatoCalendarNavHost(navController = navController)
            }
        }
    }
}
