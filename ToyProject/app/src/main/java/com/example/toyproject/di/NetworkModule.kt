package com.example.toyproject.di

import android.content.Context
import android.content.SharedPreferences
import com.example.toyproject.BuildConfig
import com.example.toyproject.network.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideHttpClient(sharedPreferences: SharedPreferences): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor{chain : Interceptor.Chain ->
                val newRequest = chain.request().newBuilder()
                val token = sharedPreferences.getString("token","nothing")
                if(token!="nothing") {
                    newRequest.addHeader("Authorization", "JWT $token")
                }
                chain.proceed(newRequest.build())}
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level =
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE
                })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideService(retrofit: Retrofit): Service {
        return retrofit.create(Service::class.java)
    }

    @InstallIn(SingletonComponent::class)
    @Module
    object PreferenceModule {
        @Provides
        @Singleton
        fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
            return context.getSharedPreferences("ToyProject.preference", Context.MODE_PRIVATE)
        }
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideBoardService(retrofit: Retrofit): BoardService {
        return retrofit.create(BoardService::class.java)
    }

    @Provides
    @Singleton
    fun provideTableService(retrofit: Retrofit): TableService {
        return retrofit.create(TableService::class.java)
    }

    @Provides
    @Singleton
    fun provideReviewService(retrofit: Retrofit): ReviewService {
        return retrofit.create(ReviewService::class.java)
    }

    @Provides
    @Singleton
    fun provideNoteService(retrofit: Retrofit): NoteService {
        return retrofit.create(NoteService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotifyService(retrofit: Retrofit): NotifyService {
        return retrofit.create(NotifyService::class.java)
    }
}