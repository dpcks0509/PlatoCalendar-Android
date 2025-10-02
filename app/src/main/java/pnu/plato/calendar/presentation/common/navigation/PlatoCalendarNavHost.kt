package pnu.plato.calendar.presentation.common.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import pnu.plato.calendar.presentation.calendar.CalendarScreen
import pnu.plato.calendar.presentation.common.component.WebView
import pnu.plato.calendar.presentation.common.navigation.PlatoCalendarScreen.CalendarScreen
import pnu.plato.calendar.presentation.common.navigation.PlatoCalendarScreen.SettingScreen
import pnu.plato.calendar.presentation.common.navigation.PlatoCalendarScreen.ToDoScreen
import pnu.plato.calendar.presentation.common.navigation.PlatoCalendarScreen.WebView
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
        composable<CalendarScreen>(
            enterTransition = { ordinalSlideEnter() },
            exitTransition = { ordinalSlideExit() },
            popEnterTransition = { ordinalSlideEnter() },
            popExitTransition = { ordinalSlideExit() },
        ) {
            CalendarScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable<ToDoScreen>(
            enterTransition = { ordinalSlideEnter() },
            exitTransition = { ordinalSlideExit() },
            popEnterTransition = { ordinalSlideEnter() },
            popExitTransition = { ordinalSlideExit() },
        ) {
            ToDoScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable<SettingScreen>(
            enterTransition = { ordinalSlideEnter() },
            exitTransition = { ordinalSlideExit() },
            popEnterTransition = { ordinalSlideEnter() },
            popExitTransition = { ordinalSlideExit() },
        ) {
            SettingScreen(
                navigateToWebView = { url -> navController.navigate(WebView(url)) },
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable<WebView> {
            val url = it.toRoute<WebView>().url

            WebView(
                url = url,
                modifier = Modifier.fillMaxSize().statusBarsPadding(),
            )
        }
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.computeOrdinalSlideDirection():
    AnimatedContentTransitionScope.SlideDirection? {
    val fromIndex =
        BottomBarItem.entries.indexOfFirst { it.route::class.qualifiedName == initialState.destination.route }
    val toIndex =
        BottomBarItem.entries.indexOfFirst { it.route::class.qualifiedName == targetState.destination.route }
    if (fromIndex == -1 || toIndex == -1) return null
    return if (fromIndex < toIndex) {
        AnimatedContentTransitionScope.SlideDirection.Left
    } else {
        AnimatedContentTransitionScope.SlideDirection.Right
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.ordinalSlideEnter(): EnterTransition? {
    val direction = computeOrdinalSlideDirection() ?: return null
    return slideIntoContainer(direction, animationSpec = tween())
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.ordinalSlideExit(): ExitTransition? {
    val direction = computeOrdinalSlideDirection() ?: return null
    return slideOutOfContainer(direction, animationSpec = tween())
}
