package com.example.gutenberglibrary.AppScreens

import android.Manifest
import android.app.Activity.RECEIVER_EXPORTED
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
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
    val availableLanguages = listOf("pl", "en", "de", "fr", "ru")
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
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text("Search Options")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.outline)

            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    TextField(
                        value = searchBar,
                        onValueChange = { libMVVM.updateSearchBar(it) },
                        label = { Text("Enter Text") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                            focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                            unfocusedLabelColor =MaterialTheme.colorScheme.surface,
                            focusedLabelColor = MaterialTheme.colorScheme.surface,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                            cursorColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    TextField(
                        value = topicBar,
                        onValueChange = { libMVVM.updateTopicBar(it) },
                        label = { Text("Enter Topic") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                            focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                            unfocusedLabelColor =MaterialTheme.colorScheme.surface,
                            focusedLabelColor = MaterialTheme.colorScheme.surface,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                            cursorColor = MaterialTheme.colorScheme.surface
                        )
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
                                Text(language)
                            }
                        }
                    }
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RectangleShape,
                    onClick = {

                        val resSearch = searchBar.replace(" ", "%20")
                        val resTopic = topicBar.replace(" ", "%20")
                        val resLanguages : String = if (selectedLanguages.isNotEmpty()){
                            selectedLanguages.joinToString(",")
                        }else{
                            availableLanguages.joinToString(",")
                        }

                        libMVVM.downloadBooksFromApi(
                            page = 1,
                            search = resSearch,
                            language = resLanguages,
                            topic = resTopic
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.surface
                    )
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
                BookRecord(book,libMVVM,context,bookServiceIntent)
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
                    val resSearch = searchBar.replace(" ", "%20")
                    val resTopic = topicBar.replace(" ", "%20")
                    val resLanguages : String = if (selectedLanguages.isNotEmpty()){
                        selectedLanguages.joinToString(",")
                    }else{
                        availableLanguages.joinToString(",")
                    }
                    val rpage = libMVVM.currentLibraryPage.value - 1

                        libMVVM.updateCurrentPage(page)
                        libMVVM.downloadBooksFromApi(
                        page = rpage,
                        search = resSearch,
                        language = resLanguages,
                        topic = resTopic
                    )
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ArrowBack", tint = MaterialTheme.colorScheme.surface)
            }
            Spacer(modifier = Modifier.width(16.dp))

            Text("$page/$pageCount", Modifier.padding(12.dp), style = TextStyle(color = MaterialTheme.colorScheme.surface))


            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = {
                    val resSearch = searchBar.replace(" ", "%20")
                    val resTopic = topicBar.replace(" ", "%20")
                    val resLanguages : String = if (selectedLanguages.isNotEmpty()){
                        selectedLanguages.joinToString(",")
                    }else{
                        availableLanguages.joinToString(",")
                    }
                    val rpage = libMVVM.currentLibraryPage.value + 1
                    libMVVM.updateCurrentPage(page)
                    libMVVM.downloadBooksFromApi(
                        page = rpage,
                        search = resSearch,
                        language = resLanguages,
                        topic = resTopic
                    )
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "ArrowForward",
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}

private fun checkNotificationPermission(context: Context): Boolean{
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else{
        true
    }
}



@Composable
fun BookRecord(book: LibraryBookInfo,libMVVM: LibraryViewModel, context: Context,bookServiceIntent: Intent) {
    val currentUser by libMVVM.currentUser.collectAsState()

    var permission by remember { mutableStateOf(checkNotificationPermission(context)) }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permission  = isGranted
    }


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
                try {
                    libMVVM.addUserBookToRepo(currentUser!!, bookWithContent)
                }catch (e : Exception){
                    Log.d("download method ---->" , "wrong method")
                }finally {
                    if (permission){
                        val builder = NotificationCompat.Builder(context,"default_channel")
                            .setSmallIcon(R.drawable.lib)
                            .setContentTitle("New book in collection")
                            .setContentText("Book \"${book.title}\" successfully saved in your repository.")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setStyle(NotificationCompat.BigTextStyle())
                            .setColor(0xFFBA8265.toInt())
                        with(NotificationManagerCompat.from(context)){
                            notify(1,builder.build())
                        }
                    }
                }

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
            .border(3.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = book.title,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Author: ${book.author}",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Bookshelves:",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.surface),
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
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.surface),
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
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    bookServiceIntent.putExtra("ID", book.id)
                    bookServiceIntent.putExtra("DOWNLOAD", "cloud")
                    context.startService(bookServiceIntent)
                },
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.outline,
                    contentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.Transparent
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cloud),
                    contentDescription = "To Cloud",
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
            .border(3.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp))
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        Text(
            text = genre,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.surface
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

