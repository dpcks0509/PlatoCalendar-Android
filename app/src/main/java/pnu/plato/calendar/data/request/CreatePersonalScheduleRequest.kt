package pnu.plato.calendar.data.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePersonalScheduleRequest(
    val index: Int = 0,
    @SerializedName("methodname")
    val methodName: String = "core_calendar_submit_create_update_form",
    val args: CreatePersonalScheduleArgs,
)

@Serializable
data class CreatePersonalScheduleArgs(
    @SerializedName("formdata")
    val formData: String,
)
