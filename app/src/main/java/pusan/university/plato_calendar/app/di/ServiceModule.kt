package pusan.university.plato_calendar.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pusan.university.plato_calendar.data.remote.service.CalendarService
import pusan.university.plato_calendar.data.remote.service.LoginService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideLoginService(retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }

    @Provides
    @Singleton
    fun provideCalendarService(retrofit: Retrofit): CalendarService {
        return retrofit.create(CalendarService::class.java)
    }
}