package pusan.university.plato_calendar.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pusan.university.plato_calendar.data.local.repository.LocalCourseRepository
import pusan.university.plato_calendar.data.remote.repository.RemoteCalendarRepository
import pusan.university.plato_calendar.data.remote.repository.RemoteLoginRepository
import pusan.university.plato_calendar.domain.repository.CalendarRepository
import pusan.university.plato_calendar.domain.repository.LoginRepository
import pusan.university.plato_calendar.domain.repository.CourseRepository
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