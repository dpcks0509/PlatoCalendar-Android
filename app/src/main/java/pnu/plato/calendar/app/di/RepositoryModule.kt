package pnu.plato.calendar.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pnu.plato.calendar.data.local.repository.LocalCourseRepository
import pnu.plato.calendar.data.remote.repository.RemoteCalendarRepository
import pnu.plato.calendar.data.remote.repository.RemoteLoginRepository
import pnu.plato.calendar.domain.repository.CalendarRepository
import pnu.plato.calendar.domain.repository.LoginRepository
import pnu.plato.calendar.domain.repository.CourseRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCourseRepository(courseRepositoryImpl: LocalCourseRepository): CourseRepository

    @Binds
    @Singleton
    abstract fun bindCalendarRepository(calendarRepositoryImpl: RemoteCalendarRepository): CalendarRepository

    @Binds
    @Singleton
    abstract fun bindLoginRepository(loginRepositoryImpl: RemoteLoginRepository): LoginRepository
}