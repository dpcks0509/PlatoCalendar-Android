package pusan.university.plato_calendar.data.remote.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

private const val CALENDAR_BASE_URL = "calendar/"

interface CalendarService {
    @FormUrlEncoded
    @POST(CALENDAR_BASE_URL + "export.php")
    suspend fun getSchedules(
        @Field("sesskey") sessKey: String,
        @Field("_qf__core_calendar_export_form") form: String = "1",
        @Field("events[exportevents]") exportEvents: String = "all",
        @Field("period[timeperiod]") timePeriod: String = "recentupcoming",
        @Field("export") export: String = "내보내기"
    ): Response<ResponseBody>
}