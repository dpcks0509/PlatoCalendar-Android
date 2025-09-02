package pnu.dpcks0509.plato_calendar.data.repository.remote.service

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

private const val LOGIN_BASE_URL = "login/"

interface LoginService {
    @FormUrlEncoded
    @POST(LOGIN_BASE_URL + "index.php")
    suspend fun login(
        @Header("Content-Length") contentLength: String,
        @Field("username") userName: String,
        @Field("password") password: String,
    ): Response<Unit>

    @GET(LOGIN_BASE_URL + "logout.php")
    suspend fun logout(@Query("sesskey") sessKey: String): Response<Unit>
}