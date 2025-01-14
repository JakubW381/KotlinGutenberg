package com.example.gutenberglibrary.BookInfo

import com.google.gson.annotations.SerializedName

data class BookList (
    @SerializedName("count") val count : Int,
    @SerializedName("results") val results : List<BookInfo>
)
data class BookInfo(
    @SerializedName("id") val id : Int,
    @SerializedName("title") val title : String,
    @SerializedName("authors") val authors : List<AuthorInfo>,
    @SerializedName("bookshelves") val bookshelves : List<String>,
    @SerializedName("languages") val languages : List<String>,
)
data class AuthorInfo(
    @SerializedName("name") val name : String
)