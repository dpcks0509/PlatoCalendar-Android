package pnu.dpcks0509.plato_calendar.di

import com.example.plato_calendar.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val PLAT_BASE_URL = BuildConfig.PLATO_BASE_URL

    @Gson
    @Singleton
    @Provides
    fun providesGsonRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(PLAT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Scalars
    @Singleton
    @Provides
    fun providesScalarsRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(PLAT_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Gson

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Scalars