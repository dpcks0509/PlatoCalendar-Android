package pnu.plato.calendar.presentation.common.eventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

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
