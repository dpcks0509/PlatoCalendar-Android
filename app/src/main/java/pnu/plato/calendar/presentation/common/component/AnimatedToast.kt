package pnu.plato.calendar.presentation.common.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import pnu.plato.calendar.R
import pnu.plato.calendar.presentation.common.eventbus.SnackbarEventBus
import pnu.plato.calendar.presentation.common.eventbus.SnackbarMessage

@Composable
fun AnimatedToast(modifier: Modifier = Modifier) {
    var currentMessage by remember { mutableStateOf<SnackbarMessage?>(null) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        SnackbarEventBus.snackbarMessage.collect { message ->
            currentMessage = message
            delay(100)
            isVisible = true

            delay(3000)
            isVisible = false

            delay(300)
            currentMessage = null
        }
    }

    currentMessage?.let { message ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier =
                modifier
                    .statusBarsPadding()
                    .padding(top = 16.dp),
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(60.dp))
                            .background(Color(0xA6000000))
                            .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    val icon =
                        when (message) {
                            is SnackbarMessage.Error -> painterResource(R.drawable.ic_error)
                            is SnackbarMessage.Success -> painterResource(R.drawable.ic_success)
                        }

                    val iconTint =
                        when (message) {
                            is SnackbarMessage.Error -> Color(0xFFED4552)
                            is SnackbarMessage.Success -> Color(0xFF4CAF50)
                        }

                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp),
                    )

                    Text(
                        modifier = Modifier.padding(vertical = 14.dp),
                        text = message.message,
                        style =
                            TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFFFFFFFF),
                                textAlign = TextAlign.Center,
                            ),
                    )
                }
            }
        }
    }
}
