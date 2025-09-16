package pnu.plato.calendar.data.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class DeletePersonalScheduleRequest(
    val index: Int = 0,
    @SerializedName("methodname")
    val methodName: String = "core_calendar_delete_calendar_events",
    val args: DeletePersonalScheduleArgs,
)

@Serializable
data class DeletePersonalScheduleArgs(
    val events: List<DeletePersonalScheduleEvent>,
)

@Serializable
data class DeletePersonalScheduleEvent(
    @SerializedName("eventid")
    val eventId: Long,
    val repeat: Boolean,
)
