package pnu.plato.calendar.data.remote.service

import okhttp3.ResponseBody
import pnu.plato.calendar.data.remote.request.CreateRequest
import pnu.plato.calendar.data.remote.request.DeleteRequest
import pnu.plato.calendar.data.remote.request.UpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface PersonalScheduleService {
    @FormUrlEncoded
    @POST("/calendar/export.php")
    suspend fun getPersonalSchedules(
        @Field("sesskey") sessKey: String,
        @Field("_qf__core_calendar_export_form") form: String = "1",
        @Field("events[exportevents]") exportEvents: String = "all",
        @Field("period[timeperiod]") timePeriod: String = "recentupcoming",
        @Field("export") export: String = "내보내기",
    ): Response<ResponseBody>

    @POST("/lib/ajax/service.php")
    suspend fun createPersonalSchedule(
        @Query("sesskey") sessKey: String,
        @Query("info") info: String = "core_calendar_submit_create_update_form",
        @Body body: List<CreateRequest>,
    ): Response<ResponseBody>

    @POST("/lib/ajax/service.php")
    suspend fun updatePersonalSchedule(
        @Query("sesskey") sessKey: String,
        @Query("info") info: String = "core_calendar_submit_create_update_form",
        @Body body: List<UpdateRequest>,
    ): Response<ResponseBody>

    @POST("/lib/ajax/service.php")
    suspend fun deletePersonalSchedule(
        @Query("sesskey") sessKey: String,
        @Query("info") info: String = "core_calendar_delete_calendar_events",
        @Body body: List<DeleteRequest>,
    ): Response<ResponseBody>
}
