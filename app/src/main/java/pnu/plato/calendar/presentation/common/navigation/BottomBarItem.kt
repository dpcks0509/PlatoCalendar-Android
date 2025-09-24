package pnu.plato.calendar.presentation.common.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import pnu.plato.calendar.R
import pnu.plato.calendar.presentation.common.navigation.PlatoCalendarScreen.CalendarScreen
import pnu.plato.calendar.presentation.common.navigation.PlatoCalendarScreen.SettingScreen
import pnu.plato.calendar.presentation.common.navigation.PlatoCalendarScreen.ToDoScreen

enum class BottomBarItem(
    val route: PlatoCalendarScreen,
    val icon: ImageVector,
    @StringRes val titleRes: Int,
) {
    CALENDAR(
        route = CalendarScreen,
        icon = Icons.Default.DateRange,
        titleRes = R.string.calendar,
    ),
    TODO(
        route = ToDoScreen,
        icon = Icons.AutoMirrored.Filled.List,
        titleRes = R.string.to_do,
    ),
    SETTING(
        route = SettingScreen,
        icon = Icons.Default.Settings,
        titleRes = R.string.setting,
    ),
}
