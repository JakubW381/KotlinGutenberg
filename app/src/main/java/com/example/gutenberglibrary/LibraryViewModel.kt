package com.example.gutenberglibrary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gutenberglibrary.BookInfo.BookUserRepository
import com.example.gutenberglibrary.BookInfo.BookWithContent
import com.example.gutenberglibrary.BookInfo.LibraryBookInfo
import com.example.gutenberglibrary.BookInfo.RetrofitBookInfoFetch
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language

class LibraryViewModel : ViewModel() {
    private val fireStoreRepo = BookUserRepository()
    private val _currentUser = MutableStateFlow<FirebaseUser?>(Firebase.auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    fun updateCurrentUser(user: FirebaseUser?) {
        viewModelScope.launch {
            _currentUser.value = user
        }
    }

    private val _userRepoBooks = MutableStateFlow<List<BookWithContent>>(emptyList())
    val userRepoBooks: StateFlow<List<BookWithContent>> = _userRepoBooks

    fun getUserRepoBooks(user :FirebaseUser){
        viewModelScope.launch {
            _userRepoBooks.value = fireStoreRepo.getUserBooks(user)
        }
    }
    fun addUserBookToRepo(user :FirebaseUser,bookWithContent: BookWithContent){
        viewModelScope.launch {
           fireStoreRepo.addUserBook(user,bookWithContent)
            getUserRepoBooks(user)
        }
    }
    fun deleteUserBookFromRepo(user :FirebaseUser,bookID : Int){
        viewModelScope.launch {
            fireStoreRepo.deleteUserBook(user, bookID)
            getUserRepoBooks(user)
        }
    }







    private val _currentLibraryBooks = MutableStateFlow<List<LibraryBookInfo>>(emptyList())
    val currentLibraryBooks: StateFlow<List<LibraryBookInfo>> = _currentLibraryBooks

    fun updateCurrentLibraryBooks(list: List<LibraryBookInfo>) {
        viewModelScope.launch {
            _currentLibraryBooks.value = list
        }
    }
    private val _currentLibraryPage = MutableStateFlow<Int>(1)
    val currentLibraryPage: StateFlow<Int> = _currentLibraryPage
    fun updateCurrentPage(page: Int) {
        if (page >= 1 && page <= pageCount.value){
            _currentLibraryPage.value = page
        }
    }

    private val _pageCount = MutableStateFlow<Int>(1)
    val pageCount: StateFlow<Int> = _pageCount
    fun updatePageCount(page: Int) {
        _pageCount.value = page
    }
    private val _searchBar = MutableStateFlow<String>("")
    val searchBar: StateFlow<String> = _searchBar
    fun updateSearchBar(value: String) {
        _searchBar.value = value
    }
    private val _topicBar = MutableStateFlow<String>("")
    val topicBar: StateFlow<String> = _topicBar
    fun updateTopicBar(value: String) {
        _topicBar.value = value
    }

    fun downloadBooksFromApi(page: Int = 1, search : String = "", language: String = "en", topic : String =""){
        Log.d("args ->","page = $page  search = $search  lang = $language topic = $topic")
        viewModelScope.launch {
            try {
                val response = RetrofitBookInfoFetch.api.getBookInfo(page,search,language,topic)

                Log.d("response ->","API Response: $response")

                if (response.results.isEmpty()) {
                    Log.d("response ->","Brak książek w odpowiedzi API")
                }

                updatePageCount((response.count/32)+1)
                var books : List<LibraryBookInfo> = emptyList()
                response.results.forEach{ result ->

                    var topics : ArrayList<String> = ArrayList<String>()
                    result.bookshelves.forEach{ topic ->
                        //topics = topics.plusElement(topic.replaceFirst("Browsing: " ,""))
                        topics = topics.plus(topic.replaceFirst("Browsing: ","")) as ArrayList<String>
                    }
                    val authorName = if (result.authors.isNotEmpty()) result.authors[0].name else "Unknown Author"
                    var book = LibraryBookInfo(
                        id = result.id,
                        title = result.title,
                        author = authorName,
                        bookshelves = topics,
                        languages = result.languages as ArrayList<String>
                    )
                    books = books.plusElement(book)
                }
                updateCurrentLibraryBooks(books)
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    private val _currentScreen = MutableStateFlow( 0)
    val currentScreen = _currentScreen.asStateFlow()

    fun updateScreen(screen: Int) {
        _currentScreen.value = screen
    }
}