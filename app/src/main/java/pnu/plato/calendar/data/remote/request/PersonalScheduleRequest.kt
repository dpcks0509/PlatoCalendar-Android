package pnu.plato.calendar.data.remote.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateRequest(
    val index: Int = 0,
    @SerializedName("methodname")
    val methodName: String = "core_calendar_submit_create_update_form",
    val args: CreateArgs,
)

@Serializable
data class CreateArgs(
    @SerializedName("formdata")
    val formData: String,
)

@Serializable
data class UpdateRequest(
    val index: Int = 0,
    @SerializedName("methodname")
    val methodName: String = "core_calendar_submit_create_update_form",
    val args: UpdateArgs,
)

@Serializable
data class UpdateArgs(
    @SerializedName("formdata")
    val formData: String,
)

@Serializable
data class DeleteRequest(
    val index: Int = 0,
    @SerializedName("methodname")
    val methodName: String = "core_calendar_delete_calendar_events",
    val args: DeleteArgs,
)

@Serializable
data class DeleteArgs(
    val events: List<DeleteEvent>,
)

@Serializable
data class DeleteEvent(
    @SerializedName("eventid")
    val eventId: Long,
    val repeat: Boolean,
)
