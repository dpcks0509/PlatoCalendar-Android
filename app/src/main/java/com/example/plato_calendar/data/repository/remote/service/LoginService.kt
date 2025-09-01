package com.example.plato_calendar.data.repository.remote.service

import retrofit2.http.GET
import retrofit2.http.POST

private const val LOGIN_BASE_URL = "login/"

interface LoginService {
    @POST(LOGIN_BASE_URL + "index.php")
    fun login(username: String, password: String): Result<Unit>

    @GET(LOGIN_BASE_URL + "logout.php")
    fun logout(sessKey: String): Result<Unit>
}