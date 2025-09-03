package pusan.university.plato_calendar.presentation.common.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pusan.university.plato_calendar.presentation.calendar.CalendarScreen
import pusan.university.plato_calendar.presentation.cafeteria.CafeteriaScreen
import pusan.university.plato_calendar.presentation.common.navigation.PlatoCalendarScreen.*
import pusan.university.plato_calendar.presentation.setting.SettingScreen
import pusan.university.plato_calendar.presentation.todo.ToDoScreen

@Composable
fun PlatoCalendarNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = CalendarScreen
    ) {
        composable<CalendarScreen> {
            CalendarScreen()
        }

        composable<ToDoScreen> {
            ToDoScreen()
        }

        composable<CafeteriaScreen> {
            CafeteriaScreen()
        }

        composable<SettingScreen> {
            SettingScreen()
        }
    }
}