package com.example.gutenberglibrary.AppScreens


import android.app.Activity.RECEIVER_EXPORTED
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gutenberglibrary.BookInfo.BookWithContent
import com.example.gutenberglibrary.BookInfo.LibraryBookInfo
import com.example.gutenberglibrary.BookService.BookBroadcastReceiver
import com.example.gutenberglibrary.BookService.BookService
import com.example.gutenberglibrary.LibraryViewModel
import com.example.gutenberglibrary.R


@Composable
fun LibraryScreen(libMVVM: LibraryViewModel,context : Context) {
    val books by libMVVM.currentLibraryBooks.collectAsState(initial = emptyList())
    val pageCount by libMVVM.pageCount.collectAsState(1)
    val page by libMVVM.currentLibraryPage.collectAsState()

    val bookServiceIntent = Intent(context,BookService::class.java)


    val searchBar by libMVVM.searchBar.collectAsState("")
    val topicBar by libMVVM.topicBar.collectAsState("")

    var expanded by remember { mutableStateOf(false) }
    var availableLanguages = listOf("pl", "en", "de", "fr", "ru")
    var selectedLanguages by remember { mutableStateOf(setOf<String>()) }



    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {


            Button(
                onClick = { expanded = !expanded },
                modifier = Modifier
                    .padding(0.dp)
                    .fillMaxWidth(),
                shape = RectangleShape
            ) {
                Text("Search Options")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = searchBar,
                        onValueChange = { libMVVM.updateSearchBar(it) },
                        label = { Text("Enter Text") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    TextField(
                        value = topicBar,
                        onValueChange = { libMVVM.updateTopicBar(it) },
                        label = { Text("Enter Topic") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    Text("Choose your languages:")
                    Row {
                        availableLanguages.forEach { language ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .weight(1f)
                            ) {
                                Checkbox(
                                    checked = selectedLanguages.contains(language),
                                    onCheckedChange = {
                                        selectedLanguages = if (it) {
                                            selectedLanguages + language
                                        } else {
                                            selectedLanguages - language
                                        }
                                    }
                                )
                                Text("$language")
                            }
                        }
                    }
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RectangleShape,
                    onClick = {

                        var resSearch = searchBar.replace(" ", "%20")
                        var resTopic = topicBar.replace(" ", "%20")
                        var resLanguages : String
                        if (selectedLanguages.isNotEmpty()){
                            resLanguages = selectedLanguages.joinToString(",")
                        }else{
                            resLanguages = availableLanguages.joinToString(",")
                        }

                        libMVVM.downloadBooksFromApi(
                            page = 1,
                            search = resSearch,
                            language = resLanguages,
                            topic = resTopic
                        )
                    }
                ) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            }
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp, bottom = 70.dp)
        ) {
            items(books) { book ->
                BookRecord(book,libMVVM,context,bookServiceIntent) //TODO: toCloud, toStorage
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    var resSearch = searchBar.replace(" ", "%20")
                    var resTopic = topicBar.replace(" ", "%20")
                    var resLanguages : String
                    if (selectedLanguages.isNotEmpty()){
                        resLanguages = selectedLanguages.joinToString(",")
                    }else{
                        resLanguages = availableLanguages.joinToString(",")
                    }
                    val page = libMVVM.currentLibraryPage.value - 1;

                        libMVVM.updateCurrentPage(page)
                        libMVVM.downloadBooksFromApi(
                        page = page,
                        search = resSearch,
                        language = resLanguages,
                        topic = resTopic
                    )
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ArrowBack")
            }
            Spacer(modifier = Modifier.width(16.dp))

            Text("$page/$pageCount", Modifier.padding(12.dp))


            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = {
                    var resSearch = searchBar.replace(" ", "%20")
                    var resTopic = topicBar.replace(" ", "%20")
                    var resLanguages : String
                    if (selectedLanguages.isNotEmpty()){
                        resLanguages = selectedLanguages.joinToString(",")
                    }else{
                        resLanguages = availableLanguages.joinToString(",")
                    }
                    val page = libMVVM.currentLibraryPage.value + 1
                    libMVVM.updateCurrentPage(page)
                    libMVVM.downloadBooksFromApi(
                        page = page,
                        search = resSearch,
                        language = resLanguages,
                        topic = resTopic
                    )
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "ArrowForward"
                )
            }
        }
    }
}


@Composable
fun BookRecord(book: LibraryBookInfo,libMVVM: LibraryViewModel, context: Context,bookServiceIntent: Intent) {
    val currentUser by libMVVM.currentUser.collectAsState()

    DisposableEffect(Unit) {
        val receiver = BookBroadcastReceiver { received ->
            if (book.id == received.id){
                val bookWithContent = BookWithContent(
                    id = book.id,
                    title = book.title,
                    author = book.author,
                    bookshelves = book.bookshelves,
                    languages = book.languages,
                    content = received.content
                )
                libMVVM.addUserBookToRepo(currentUser!!, bookWithContent)
            }
        }
        val intentFilter = IntentFilter("com.example.gutenberglibrary.BOOK_DOWNLOADED")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(receiver, intentFilter)
        }
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp)
            //.clickable { Log.d("click ------>","book info: ${book.id} ,${book.author} ,${book.title} ,${book.bookshelves.firstOrNull() ?: ""} , ") }
    ) {
        Text(
            text = book.title,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Author: ${book.author}",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Bookshelves:",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            book.bookshelves.forEach { shelf ->
                GenreTag(genre = shelf)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Languages:",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            book.languages.forEach { language ->
                GenreTag(genre = language)
            }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    bookServiceIntent.putExtra("ID", book.id)
                    context.startService(bookServiceIntent)
                },
                colors = ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.Transparent
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cloud),
                    contentDescription = "Download",
                    modifier = Modifier.size(40.dp)
                )
            }
            Button(
                onClick = {

                },
                colors = ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.Transparent
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.download),
                    contentDescription = "Download",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
fun GenreTag(genre: String) {
    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .border(1.dp, Color(0xFF6200EE), RoundedCornerShape(16.dp))
            .background(Color(0xFF6200EE).copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        Text(
            text = genre,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF6200EE)
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

data class Topics(
    val categories: List<String> = listOf(
        "Archaeology",
        "Architecture",
        "Art & Photography",
        "Biographies",
        "Business/Management",
        "Children & Young Adult Reading",
        "Computers & Technology",
        "Cooking & Drinking",
        "Culture/Civilization/Society",
        "Crime/Mystery",
        "Drugs/Alcohol/Pharmacology",
        "Economics",
        "Encyclopedias/Dictionaries/Reference",
        "Engineering & Construction",
        "Environmental Issues",
        "Fashion & Costume",
        "Fiction",
        "Gender & Sexuality Studies",
        "Health & Medicine",
        "History - American",
        "History - Ancient",
        "History - British",
        "History - European",
        "History - General",
        "History - Medieval/The Middle Ages",
        "History - Religious",
        "History - Royalty",
        "History - Schools & Universities",
        "History - Warfare",
        "How To...",
        "Humour",
        "Journalism/Media/Writing",
        "Journals",
        "Language & Communication",
        "Law & Criminology",
        "Literature",
        "Mathematics",
        "Music",
        "Nature/Gardening/Animals",
        "Nutrition",
        "Old Age & the Elderly",
        "Other",
        "Parenthood & Family Relations",
        "Performing Arts/Film",
        "Philosophy & Ethics",
        "Poetry",
        "Politics",
        "Psychiatry/Psychology",
        "Religion/Spirituality/Paranormal",
        "Reports & Conference Proceedings",
        "Research Methods/Statistics/Information Sys",
        "Russian Interest",
        "Science - Astronomy",
        "Science - Chemistry/Biochemistry/Physics",
        "Science - Earth/Agricultural/Farming",
        "Science - Physics",
        "Science - General",
        "Science - Genetics/Biology/Evolution",
        "Science-Fiction & Fantasy",
        "Sexuality & Erotica",
        "Sociology",
        "Sports/Hobbies/Motoring",
        "Teaching & Education",
        "Travel & Geography"
    )
)

