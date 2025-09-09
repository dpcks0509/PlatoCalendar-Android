package pnu.plato.calendar.data.remote.service

import kotlinx.serialization.json.JsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
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

    @POST("/lib/ajax/service.php")
    suspend fun updatePersonalSchedule(
        @Query("sesskey") sessKey: String,
        @Query("info") info: String = "core_calendar_submit_create_update_form",
        @Body() body: JsonObject,
    ): Response<ResponseBody>

    @POST("/lib/ajax/service.php")
    suspend fun deletePersonalSchedule(
        @Query("sesskey") sessKey: String,
        @Query("info") info: String = "core_calendar_delete_calendar_events",
        @Body() body: JsonObject,
    ): Response<ResponseBody>
}
/*
UPDATE

[{
    "index":0,
    "methodname":"core_calendar_submit_create_update_form",
    "args":{
        "formdata":
        "id=5240580&" +
                "userid=399165&" +
                "modulename=0&" +
                "instance=0&" +
                "visible=1&" +
                "eventtype=user&" +
                "repeatid=0&" +
                "sesskey=mPtqrMHjp3&" +
                "_qf__core_calendar_local_event_forms_update=1&" +
                "mform_showmore_id_general=0&" +
                "name=2134&" +
                "timestart%5Byear%5D=2025&" +
                "timestart%5Bday%5D=6&" +
                "timestart%5Bmonth%5D=9&" +
                "timestart%5Bhour%5D=22&" +
                "timestart%5Bminute%5D=51&" +
                "description%5Btext%5D=%3Cp%3E21232133%3C%2Fp%3E&" +
                "description%5Bformat%5D=1&" +
                "description%5Bitemid%5D=759600809&" +
                "duration=1&" +
                "timedurationuntil%5Byear%5D=2025&" +
                "timedurationuntil%5Bmonth%5D=9&" +
                "timedurationuntil%5Bday%5D=11&" +
                "timedurationuntil%5Bhour%5D=22&" +
                "timedurationuntil%5Bminute%5D=51"
    }
}]

id=5240580
userid=399165
sesskey=mPtqrMHjp3
name=2134
timestart%5Byear%5D=2025
timestart%5Bday%5D=6
timestart%5Bmonth%5D=9
timestart%5Bhour%5D=22
timestart%5Bminute%5D=51
description%5Btext%5D=%3Cp%3E "21232133" %3C%2Fp%3E  -> ("21232133" 은 파라미터로 받아오는 값)
timedurationuntil%5Byear%5D=2025
timedurationuntil%5Bmonth%5D=9
timedurationuntil%5Bday%5D=11
timedurationuntil%5Bhour%5D=22
timedurationuntil%5Bminute%5D=51


DELETE

[{"index":0,"methodname":"core_calendar_delete_calendar_events","args":{"events":[{"eventid":5240580,"repeat":false}]}}]
 */