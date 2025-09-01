package com.example.plato_calendar.di

import com.example.plato_calendar.data.repository.local.LocalSubjectRepository
import com.example.plato_calendar.data.repository.remote.RemoteCalendarRepository
import com.example.plato_calendar.domain.repository.CalendarRepository
import com.example.plato_calendar.domain.repository.LoginRepository
import com.example.plato_calendar.domain.repository.SubjectRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsSubjectRepository(subjectRepositoryImpl: LocalSubjectRepository): SubjectRepository

    @Binds
    @Singleton
    abstract fun bindsCalendarRepository(calendarRepositoryImpl: RemoteCalendarRepository): CalendarRepository

    @Binds
    @Singleton
    abstract fun bindsLoginRepository(loginRepositoryImpl: LoginRepository): LoginRepository
}