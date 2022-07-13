package com.doxx.writein.di

import com.doxx.writein.api.AuthIntercepter
import com.doxx.writein.api.NotesAPI
import com.doxx.writein.api.UserAPI
import com.doxx.writein.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module

class NetworkModule {
    @Singleton
    @Provides
    fun getRetrofit():Retrofit.Builder{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)

    }

    @Singleton
    @Provides
    fun getUserAPI(retrofitBuilder: Retrofit.Builder):UserAPI{
        return retrofitBuilder.build().create(UserAPI::class.java)
    }

    @Singleton
    @Provides
    fun getOkHttpClient(authIntercepter: AuthIntercepter):OkHttpClient{
        return OkHttpClient.Builder().addInterceptor(authIntercepter).build()
    }
    @Singleton
    @Provides
    fun getNotesAPI(retrofitBuilder: Retrofit.Builder,okHttpClient: OkHttpClient):NotesAPI{
        return retrofitBuilder.client(okHttpClient).build().create(NotesAPI::class.java)
    }


}