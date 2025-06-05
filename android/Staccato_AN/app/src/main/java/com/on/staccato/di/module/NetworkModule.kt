package com.on.staccato.di.module

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.on.staccato.BuildConfig.BASE_URL
import com.on.staccato.data.dto.ErrorResponse
import com.on.staccato.data.member.MemberLocalDataSource
import com.on.staccato.data.network.ApiResultCallAdapterFactory
import com.on.staccato.data.network.ErrorConverter
import com.on.staccato.data.network.HeaderInterceptor
import com.on.staccato.data.network.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    private const val CONTENT_TYPE = "application/json"
    private const val EXCEPTION_CONVERT_ERROR_RESPONSE = "errorBody를 변환할 수 없습니다."

    @Provides
    @Singleton
    fun provideBaseUrl(): String = BASE_URL

    @Provides
    @Singleton
    fun provideTokenManager(memberLocalDataSource: MemberLocalDataSource): TokenManager = TokenManager(memberLocalDataSource)

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            coerceInputValues = true
        }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideHeaderInterceptor(tokenManager: TokenManager): HeaderInterceptor = HeaderInterceptor(tokenManager)

    @Provides
    @Singleton
    fun provideHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        headerInterceptor: HeaderInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(headerInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideApiResultCallAdapterFactory(): ApiResultCallAdapterFactory = ApiResultCallAdapterFactory()

    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String,
        client: OkHttpClient,
        json: Json,
        callAdapterFactory: ApiResultCallAdapterFactory,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(
                json.asConverterFactory(CONTENT_TYPE.toMediaType()),
            )
            .addCallAdapterFactory(callAdapterFactory)
            .build()

    @Provides
    @Singleton
    fun provideErrorConverter(retrofit: Retrofit): ErrorConverter =
        ErrorConverter { errorBody ->
            retrofit.responseBodyConverter<ErrorResponse>(
                ErrorResponse::class.java,
                ErrorResponse::class.java.annotations,
            ).convert(errorBody) ?: throw IllegalArgumentException(EXCEPTION_CONVERT_ERROR_RESPONSE)
        }
}
