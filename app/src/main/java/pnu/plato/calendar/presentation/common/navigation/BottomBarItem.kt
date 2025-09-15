package pnu.plato.calendar.presentation.common.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import pnu.plato.calendar.R

enum class BottomBarItem(
    val route: PlatoCalendarScreen,
    val icon: ImageVector,
    @StringRes val titleRes: Int,
) {
    CALENDAR(
        route = PlatoCalendarScreen.CalendarScreen,
        icon = Icons.Default.DateRange,
        titleRes = R.string.calendar,
    ),
    TODO(
        route = PlatoCalendarScreen.ToDoScreen,
        icon = Icons.Default.List,
        titleRes = R.string.to_do,
    ),
    SETTING(
        route = PlatoCalendarScreen.SettingScreen,
        icon = Icons.Default.Settings,
        titleRes = R.string.setting,
    ),
}
