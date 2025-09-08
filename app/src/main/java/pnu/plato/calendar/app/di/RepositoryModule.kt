package pnu.plato.calendar.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pnu.plato.calendar.data.local.repository.LocalCourseRepository
import pnu.plato.calendar.data.remote.repository.RemoteLoginRepository
import pnu.plato.calendar.data.remote.repository.RemoteScheduleRepository
import pnu.plato.calendar.domain.repository.CourseRepository
import pnu.plato.calendar.domain.repository.LoginRepository
import pnu.plato.calendar.domain.repository.ScheduleRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCourseRepository(repositoryImpl: LocalCourseRepository): CourseRepository

    @Binds
    @Singleton
    abstract fun bindScheduleRepository(repositoryImpl: RemoteScheduleRepository): ScheduleRepository

    @Binds
    @Singleton
    abstract fun bindLoginRepository(repositoryImpl: RemoteLoginRepository): LoginRepository
}
