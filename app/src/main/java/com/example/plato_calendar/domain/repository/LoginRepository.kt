package com.example.plato_calendar.domain.repository

interface LoginRepository {
    fun login(username: String, password: String): Result<Unit>
    fun logout(sessKey: String): Result<Unit>
}