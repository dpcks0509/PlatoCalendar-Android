package pnu.plato.calendar.presentation.common.navigation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import pnu.plato.calendar.presentation.common.theme.Gray
import pnu.plato.calendar.presentation.common.theme.LightBlue
import pnu.plato.calendar.presentation.common.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlatoCalendarBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val noRippleConfiguration =
        RippleConfiguration(
            color = Color.Transparent,
            rippleAlpha =
                RippleAlpha(
                    0f,
                    0f,
                    0f,
                    0f,
                ),
        )

    CompositionLocalProvider(LocalRippleConfiguration provides noRippleConfiguration) {
        NavigationBar(
            modifier = Modifier.clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
        ) {
            BottomBarItem.entries.forEach { item ->
                val isSelected = currentRoute == item.route::class.qualifiedName

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(item.titleRes),
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(item.titleRes),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors =
                        NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryColor,
                            selectedTextColor = PrimaryColor,
                            unselectedIconColor = Gray,
                            unselectedTextColor = Gray,
                            indicatorColor = LightBlue,
                        ),
                )
            }
        }
    }
}
