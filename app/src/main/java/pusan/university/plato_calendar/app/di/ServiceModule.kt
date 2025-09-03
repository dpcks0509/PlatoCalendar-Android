package pusan.university.plato_calendar.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pusan.university.plato_calendar.data.repository.remote.service.CalendarService
import pusan.university.plato_calendar.data.repository.remote.service.LoginService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideLoginService(@Gson retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }

    @Provides
    @Singleton
    fun provideCalendarService(@Scalars retrofit: Retrofit): CalendarService {
        return retrofit.create(CalendarService::class.java)
    }
}