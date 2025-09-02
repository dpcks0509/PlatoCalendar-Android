package pusan.university.plato_calendar.di

import com.example.plato_calendar.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pusan.university.plato_calendar.network.InMemoryCookieStore
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val PLAT_BASE_URL = BuildConfig.PLATO_BASE_URL

    @Singleton
    @Provides
    fun providesCookieStore(): InMemoryCookieStore = InMemoryCookieStore()

    @Singleton
    @Provides
    fun providesCookieJar(cookieStore: InMemoryCookieStore): CookieJar = cookieStore

    @Singleton
    @Provides
    fun providesOkHttpClient(cookieJar: CookieJar): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .followRedirects(false)
            .followSslRedirects(false)
            .addInterceptor(logging)
            .build()
    }

    @Gson
    @Singleton
    @Provides
    fun providesGsonRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(PLAT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Scalars
    @Singleton
    @Provides
    fun providesScalarsRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(PLAT_BASE_URL)
            .client(okHttpClient)
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