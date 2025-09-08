package pnu.plato.calendar.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pnu.plato.calendar.data.remote.service.PlatoService
import pnu.plato.calendar.data.remote.service.PnuService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun providePlatoService(
        @Plato retrofit: Retrofit,
    ): PlatoService = retrofit.create(PlatoService::class.java)

    @Provides
    @Singleton
    fun providePnuService(
        @Pnu retrofit: Retrofit,
    ): PnuService = retrofit.create(PnuService::class.java)
}
