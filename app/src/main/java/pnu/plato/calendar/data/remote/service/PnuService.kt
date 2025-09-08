package pnu.plato.calendar.data.remote.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface PnuService {
    @FormUrlEncoded
    @POST("/kor/CMS/Haksailjung/view.do")
    suspend fun getAcademicSchedules(
        @Field("mCode") mCode: String = "MN076",
    ): Response<ResponseBody>
}
