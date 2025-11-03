package pusan.university.plato_calendar.data.remote.service

import okhttp3.ResponseBody
import pusan.university.plato_calendar.data.request.CreatePersonalScheduleRequest
import pusan.university.plato_calendar.data.request.DeletePersonalScheduleRequest
import pusan.university.plato_calendar.data.request.UpdatePersonalScheduleRequest
import pusan.university.plato_calendar.data.response.PersonalScheduleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface PersonalScheduleService {
    @FormUrlEncoded
    @POST("/calendar/export.php")
    suspend fun readCurrentMonthPersonalSchedules(
        @Field("sesskey") sessKey: String,
        @Field("_qf__core_calendar_export_form") form: String = "1",
        @Field("events[exportevents]") exportEvents: String = "all",
        @Field("period[timeperiod]") timePeriod: String = "monthnow",
        @Field("export") export: String = "내보내기",
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/calendar/export.php")
    suspend fun readYearPersonalSchedules(
        @Field("sesskey") sessKey: String,
        @Field("_qf__core_calendar_export_form") form: String = "1",
        @Field("events[exportevents]") exportEvents: String = "all",
        @Field("period[timeperiod]") timePeriod: String = "custom",
        @Field("export") export: String = "내보내기",
    ): Response<ResponseBody>

    @POST("/lib/ajax/service.php")
    suspend fun createCustomSchedule(
        @Query("sesskey") sessKey: String,
        @Query("info") info: String = "core_calendar_submit_create_update_form",
        @Body request: List<CreatePersonalScheduleRequest>,
    ): Response<List<PersonalScheduleResponse>>

    @POST("/lib/ajax/service.php")
    suspend fun updatePersonalSchedule(
        @Query("sesskey") sessKey: String,
        @Query("info") info: String = "core_calendar_submit_create_update_form",
        @Body request: List<UpdatePersonalScheduleRequest>,
    ): Response<List<PersonalScheduleResponse>>

    @POST("/lib/ajax/service.php")
    suspend fun deleteCustomSchedule(
        @Query("sesskey") sessKey: String,
        @Query("info") info: String = "core_calendar_delete_calendar_events",
        @Body request: List<DeletePersonalScheduleRequest>,
    ): Response<List<PersonalScheduleResponse>>
}
