package com.example.gutenberglibrary.BookInfo

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBookInfoFetch {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val api : BookInfoApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://gutendex.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BookInfoApiService::class.java)
    }
}