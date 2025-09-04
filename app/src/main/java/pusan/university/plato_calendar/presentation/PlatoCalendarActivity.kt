package pusan.university.plato_calendar.presentation

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pusan.university.plato_calendar.presentation.common.manager.LoginManager
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarNavHost
import pusan.university.plato_calendar.presentation.common.theme.PlatoCalendarTheme
import javax.inject.Inject

@AndroidEntryPoint
class PlatoCalendarActivity : ComponentActivity() {
    @Inject
    lateinit var loginManager: LoginManager

    @Inject
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            loginManager.autoLogin()
        }

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            PlatoCalendarTheme {
                PlatoCalendarNavHost(navController = navController)
            }
        }
    }
}