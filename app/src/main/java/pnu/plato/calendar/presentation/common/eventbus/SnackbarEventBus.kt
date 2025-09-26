package pnu.plato.calendar.presentation.common.eventbus

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import pnu.plato.calendar.presentation.common.theme.PrimaryColor
import pnu.plato.calendar.presentation.common.theme.Red

object SnackbarEventBus {
    private val _snackbarMessage = MutableSharedFlow<SnackbarMessage>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    suspend fun sendSuccess(message: String?) {
        if (message.isNullOrEmpty()) return

        _snackbarMessage.emit(SnackbarMessage.Success(message))
    }

    suspend fun sendError(message: String?) {
        if (message.isNullOrEmpty()) return

        _snackbarMessage.emit(SnackbarMessage.Error(message))
    }
}

sealed class SnackbarMessage {
    abstract val message: String

    data class Success(
        override val message: String,
    ) : SnackbarMessage()

    data class Error(
        override val message: String,
    ) : SnackbarMessage()
}
