package pnu.plato.calendar.presentation.common.eventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ErrorEventBus {
    private val _errorMessage = MutableSharedFlow<String?>()
    val errorMessage = _errorMessage.asSharedFlow()

    suspend fun sendError(message: String?) {
        _errorMessage.emit(message)
    }
}