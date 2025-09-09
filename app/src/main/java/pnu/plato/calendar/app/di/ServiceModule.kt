package pnu.plato.calendar.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pnu.plato.calendar.data.remote.service.AcademicScheduleService
import pnu.plato.calendar.data.remote.service.LoginService
import pnu.plato.calendar.data.remote.service.PersonalScheduleService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideLoginService(
        @PLATO retrofit: Retrofit,
    ): LoginService = retrofit.create(LoginService::class.java)

    @Provides
    @Singleton
    fun providePersonalScheduleService(
        @PLATO retrofit: Retrofit,
    ): PersonalScheduleService = retrofit.create(PersonalScheduleService::class.java)

    @Provides
    @Singleton
    fun provideAcademicScheduleService(
        @PNU retrofit: Retrofit,
    ): AcademicScheduleService = retrofit.create(AcademicScheduleService::class.java)
}
