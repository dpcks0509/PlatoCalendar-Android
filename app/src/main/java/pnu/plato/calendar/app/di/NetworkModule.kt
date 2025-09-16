package pnu.plato.calendar.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CookieJar
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pnu.plato.calendar.BuildConfig
import pnu.plato.calendar.BuildConfig.PLATO_BASE_URL
import pnu.plato.calendar.BuildConfig.PNU_BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideCookieManager(): CookieManager = CookieManager()

    @Singleton
    @Provides
    fun provideCookieJar(cookieManager: CookieManager): CookieJar = JavaNetCookieJar(cookieManager)

    @Singleton
    @Provides
    @Redirect
    fun provideReDirectOkHttpClient(cookieJar: CookieJar): OkHttpClient {
        val logging =
            HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            }

        return OkHttpClient
            .Builder()
            .cookieJar(cookieJar)
            .addInterceptor(logging)
            .build()
    }

    @Singleton
    @Provides
    @NonDirect
    fun provideNonDirectOkHttpClient(cookieJar: CookieJar): OkHttpClient {
        val logging =
            HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            }

        return OkHttpClient
            .Builder()
            .cookieJar(cookieJar)
            .addInterceptor(logging)
            .followRedirects(false)
            .build()
    }

    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Singleton
    @Provides
    @Plato
    fun providePlatoRetrofit(
        @Redirect okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(PLATO_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()

    @Singleton
    @Provides
    @PlatoNonDirect
    fun providePlatoNonDirectRetrofit(
        @NonDirect okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(PLATO_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()

    @Singleton
    @Provides
    @Pnu
    fun providePnuRetrofit(
        @Redirect okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(PNU_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
}

@Qualifier
annotation class Plato

@Qualifier
annotation class PlatoNonDirect

@Qualifier
annotation class Pnu

@Qualifier
annotation class NonDirect

@Qualifier
annotation class Redirect
