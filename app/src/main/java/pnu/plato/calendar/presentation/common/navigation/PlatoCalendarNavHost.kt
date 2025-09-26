package pnu.plato.calendar.presentation.common.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pnu.plato.calendar.presentation.calendar.CalendarScreen
import pnu.plato.calendar.presentation.common.navigation.PlatoCalendarScreen.CalendarScreen
import pnu.plato.calendar.presentation.common.navigation.PlatoCalendarScreen.SettingScreen
import pnu.plato.calendar.presentation.common.navigation.PlatoCalendarScreen.ToDoScreen
import pnu.plato.calendar.presentation.setting.SettingScreen
import pnu.plato.calendar.presentation.todo.ToDoScreen

@Composable
fun PlatoCalendarNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = CalendarScreen,
        modifier = modifier,
    ) {
        composable<CalendarScreen> {
            CalendarScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable<ToDoScreen> {
            ToDoScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable<SettingScreen> {
            SettingScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
