package pnu.plato.calendar.data.remote.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PlatoService {
    @FormUrlEncoded
    @POST("/login/index.php")
    suspend fun login(
        @Field("username") userName: String,
        @Field("password") password: String,
    ): Response<ResponseBody>

    @GET("/login/logout.php")
    suspend fun logout(
        @Query("sesskey") sessKey: String,
    ): Response<Unit>

    @FormUrlEncoded
    @POST("/calendar/export.php")
    suspend fun getPersonalSchedules(
        @Field("sesskey") sessKey: String,
        @Field("_qf__core_calendar_export_form") form: String = "1",
        @Field("events[exportevents]") exportEvents: String = "all",
        @Field("period[timeperiod]") timePeriod: String = "recentupcoming",
        @Field("export") export: String = "내보내기",
    ): Response<ResponseBody>
}
