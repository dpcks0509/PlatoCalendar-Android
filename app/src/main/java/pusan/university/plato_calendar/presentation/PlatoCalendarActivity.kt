package pusan.university.plato_calendar.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarNavHost
import pusan.university.plato_calendar.presentation.common.theme.PlatoCalendarAOSTheme

@AndroidEntryPoint
class PlatoCalendarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            PlatoCalendarAOSTheme {
                PlatoCalendarNavHost(navController = navController)
            }
        }
    }
}