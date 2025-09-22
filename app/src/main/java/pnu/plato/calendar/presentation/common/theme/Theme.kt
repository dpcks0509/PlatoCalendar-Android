package pnu.plato.calendar.presentation.common.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = PrimaryDark,
        onPrimary = WhiteDark,
        background = BlackDark,
        surface = BlackDark,
        onBackground = WhiteDark,
        onSurface = WhiteDark,
        secondary = LightBlueDark,
        onSecondary = WhiteDark,
        error = RedDark,
        onError = WhiteDark,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = PrimaryLight,
        onPrimary = BlackLight,
        background = WhiteLight,
        surface = WhiteLight,
        onBackground = BlackLight,
        onSurface = BlackLight,
        secondary = LightBlueLight,
        onSecondary = BlackLight,
        error = RedLight,
        onError = WhiteLight,
    )

@Composable
fun PlatoCalendarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
