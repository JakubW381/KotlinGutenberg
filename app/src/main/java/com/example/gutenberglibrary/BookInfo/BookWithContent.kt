package com.example.gutenberglibrary.BookInfo



data class BookWithContent(
    val id: Int? = null,
    val title: String? = null,
    val author: String? = null,
    val bookshelves: List<String>? = null,
    val languages: List<String>? = null,
    val content: String? = "",
    var scrollState: Int? = 0
)