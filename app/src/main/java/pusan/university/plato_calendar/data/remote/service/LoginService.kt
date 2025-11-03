package pusan.university.plato_calendar.data.remote.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginService {
    @FormUrlEncoded
    @POST("/login/index.php")
    suspend fun login(
        @Field("username") userName: String,
        @Field("password") password: String,
    ): Response<Unit>

    @GET("/login/logout.php")
    suspend fun logout(
        @Query("sesskey") sessKey: String,
    ): Response<Unit>

    @GET("/")
    suspend fun redirect(): Response<ResponseBody>
}
