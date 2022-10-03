package com.omdb.app.di


import com.omdb.app.domain.repository.MovieRepository
import com.omdb.app.network.repository.MovieRepositoryImpl
import com.omdb.app.network.services.MovieServices
import com.omdb.app.utils.Const
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class AppModules {

    @Provides
    @Named("WEB_API")
    fun provideWebAPI(): String = Const.WEB_API

    @Provides
    @Named("KEY_API")
    fun provideKeyAPI(): String = Const.KEY_API

    @Provides
    @Named("PAGE_SIZE")
    fun providePageSize(): Int = Const.PAGE_SIZE

    @Provides
    fun provideRetrofit(
        @Named("WEB_API") webAPI: String,
    ): Retrofit {
        val client = OkHttpClient
            .Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()
        return Retrofit.Builder()
            .baseUrl(webAPI)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    fun providMovieService(
        retrofit: Retrofit
    ): MovieServices = retrofit.create(MovieServices::class.java)

    @Provides
    fun provideMovieRepository(
        movieService: MovieServices,
        @Named("KEY_API") keyApi: String,
        @Named("PAGE_SIZE") pageSize: Int,
    ): MovieRepository = MovieRepositoryImpl(
        movieService = movieService,
        pageSize = pageSize,
        apiKey = keyApi
    )
}