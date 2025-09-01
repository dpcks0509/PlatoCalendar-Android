package pnu.dpcks0509.plato_calendar.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pnu.dpcks0509.plato_calendar.data.repository.remote.service.CalendarService
import pnu.dpcks0509.plato_calendar.data.repository.remote.service.LoginService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun providesLoginService(@Gson retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }

    @Provides
    @Singleton
    fun providesCalendarService(@Scalars retrofit: Retrofit): CalendarService {
        return retrofit.create(CalendarService::class.java)
    }
}