package pusan.university.plato_calendar.presentation.common.eventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ToastEventBus {
    private val _toastMessage = MutableSharedFlow<ToastMessage>(replay = 1)
    val toastMessage = _toastMessage.asSharedFlow()

    suspend fun sendSuccess(message: String?) {
        if (message.isNullOrEmpty()) return

        _toastMessage.emit(ToastMessage.Success(message))
    }

    suspend fun sendError(message: String?) {
        if (message.isNullOrEmpty()) return

        _toastMessage.emit(ToastMessage.Error(message))
    }
}

sealed class ToastMessage {
    abstract val message: String

    data class Success(
        override val message: String,
    ) : ToastMessage()

    data class Error(
        override val message: String,
    ) : ToastMessage()
}
