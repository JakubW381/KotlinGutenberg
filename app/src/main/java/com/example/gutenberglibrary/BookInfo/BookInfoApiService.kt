package com.example.gutenberglibrary.BookInfo

import retrofit2.http.GET
import retrofit2.http.Query

interface BookInfoApiService {
    @GET("books/")
    suspend fun getBookInfo(
        @Query("page") page: Int = 1,
        @Query("search") search: String = "",
        @Query("languages") languages : String = "en",
        @Query("topic") topic : String = ""
    ): BookList
}