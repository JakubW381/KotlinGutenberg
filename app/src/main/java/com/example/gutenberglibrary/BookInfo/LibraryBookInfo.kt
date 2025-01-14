package com.example.gutenberglibrary.BookInfo

data class LibraryBookInfo(
    val id: Int,
    val title: String,
    val author: String,
    val bookshelves: ArrayList<String>,
    val languages: ArrayList<String>,
)
