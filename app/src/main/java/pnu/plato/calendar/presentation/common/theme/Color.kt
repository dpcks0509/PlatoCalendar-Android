package pnu.plato.calendar.presentation.common.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryLight = Color(0xFF3B6EC7)
val PrimaryDark = Color(0xFF3B6EC7)

val WhiteLight = Color.White
val WhiteDark = Color.Black

val BlackLight = Color.Black
val BlackDark = Color.White

val LightBlueLight = Color(0xFFAAD0F5)
val LightBlueDark = Color(0xFF1E3A5F)

val LightGrayLight = Color(0xFFDDDDDD)
val LightGrayDark = Color(0xFF444444)

val MediumGrayLight = Color(0xFFCCCCCC)
val MediumGrayDark = Color(0xFF666666)

val GrayLight = Color(0x99000000)
val GrayDark = Color(0x99FFFFFF)

val RedLight = Color.Red
val RedDark = Color(0xFFFF8A80)

val CalendarFlamingoLight = Color(0xFFE67C73)
val CalendarFlamingoDark = Color(0xFFF6AEA9)

val CalendarSageLight = Color(0xFF33B679)
val CalendarSageDark = Color(0xFF81C995)

val CalendarLavenderLight = Color(0xFF7986CB)
val CalendarLavenderDark = Color(0xFF9FA8DA)

val CalendarGrape = Color(0xFF8E24AA)
val CalendarBanana = Color(0xFFF6BF26)
val CalendarTangerine = Color(0xFFF4511E)
val CalendarPeacock = Color(0xFF039BE5)
val CalendarBlueberry = Color(0xFF3F51B5)
val CalendarBasil = Color(0xFF0B8043)
val CalendarTomato = Color(0xFFD50000)
val CalendarGraphite = Color(0xFF616161)

val PrimaryColor: Color
    @Composable get() = if (isSystemInDarkTheme()) PrimaryDark else PrimaryLight

val Black: Color
    @Composable get() = if (isSystemInDarkTheme()) BlackDark else BlackLight

val White: Color
    @Composable get() = if (isSystemInDarkTheme()) WhiteDark else WhiteLight

val LightBlue: Color
    @Composable get() = if (isSystemInDarkTheme()) LightBlueDark else LightBlueLight

val LightGray: Color
    @Composable get() = if (isSystemInDarkTheme()) LightGrayDark else LightGrayLight

val MediumGray: Color
    @Composable get() = if (isSystemInDarkTheme()) MediumGrayDark else MediumGrayLight

val Gray: Color
    @Composable get() = if (isSystemInDarkTheme()) GrayDark else GrayLight

val Red: Color
    @Composable get() = if (isSystemInDarkTheme()) RedDark else RedLight

val CalendarFlamingo: Color
    @Composable get() = if (isSystemInDarkTheme()) CalendarFlamingoDark else CalendarFlamingoLight

val CalendarSage: Color
    @Composable get() = if (isSystemInDarkTheme()) CalendarSageDark else CalendarSageLight

val CalendarLavender: Color
    @Composable get() = if (isSystemInDarkTheme()) CalendarLavenderDark else CalendarLavenderLight
