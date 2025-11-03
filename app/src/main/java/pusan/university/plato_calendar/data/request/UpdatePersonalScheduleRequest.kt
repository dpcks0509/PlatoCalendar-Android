package pusan.university.plato_calendar.data.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePersonalScheduleRequest(
    val index: Int = 0,
    @SerializedName("methodname")
    val methodName: String = "core_calendar_submit_create_update_form",
    val args: UpdatePersonalScheduleArgs,
)

@Serializable
data class UpdatePersonalScheduleArgs(
    @SerializedName("formdata")
    val formData: String,
)
