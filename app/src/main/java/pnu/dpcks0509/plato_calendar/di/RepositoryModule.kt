package pnu.dpcks0509.plato_calendar.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pnu.dpcks0509.plato_calendar.data.repository.local.LocalSubjectRepository
import pnu.dpcks0509.plato_calendar.data.repository.remote.RemoteCalendarRepository
import pnu.dpcks0509.plato_calendar.data.repository.remote.RemoteLoginRepository
import pnu.dpcks0509.plato_calendar.domain.repository.CalendarRepository
import pnu.dpcks0509.plato_calendar.domain.repository.LoginRepository
import pnu.dpcks0509.plato_calendar.domain.repository.SubjectRepository
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
    abstract fun bindsLoginRepository(loginRepositoryImpl: RemoteLoginRepository): LoginRepository
}