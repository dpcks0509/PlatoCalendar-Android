package com.example.plato_calendar.data.service

import com.example.plato_calendar.BuildConfig
import retrofit2.http.GET
import retrofit2.http.POST

private const val BASE_URL = BuildConfig.PLATO_BASE_URL

interface UserService {
    @POST(BASE_URL + "login/index.php")
    fun login(username: String, password: String): Result<Unit>

    @GET(BASE_URL + "login/logout.php")
    fun logout(sessKey: String): Result<Unit>
}