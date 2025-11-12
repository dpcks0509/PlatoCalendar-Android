package pusan.university.plato_calendar.presentation.common.eventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NotificationEventBus {
    private val _events = MutableSharedFlow<NotificationEvent>(replay = 1)
    val events: SharedFlow<NotificationEvent> = _events.asSharedFlow()

    suspend fun sendEvent(event: NotificationEvent) {
        _events.emit(event)
    }
}

sealed interface NotificationEvent {
    data class OpenSchedule(val scheduleId: Long) : NotificationEvent

    data object OpenNewSchedule : NotificationEvent
}
