package com.example.plato_calendar.di

import com.example.plato_calendar.data.repository.LocalSubjectRepository
import com.example.plato_calendar.domain.subject.SubjectRepository
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
    abstract fun bindsSubjectRepository(repositoryImpl: LocalSubjectRepository): SubjectRepository
}