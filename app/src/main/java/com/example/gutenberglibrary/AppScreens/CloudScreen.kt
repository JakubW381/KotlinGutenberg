package com.example.gutenberglibrary.AppScreens

import androidx.core.content.FileProvider
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.media3.common.util.Log
import com.example.gutenberglibrary.BookActivity
import com.example.gutenberglibrary.BookInfo.BookWithContent
import com.example.gutenberglibrary.LibraryViewModel
import java.io.File


@Composable
fun CloudScreen(libMVVM: LibraryViewModel, context : Context) {
    val userBooks by libMVVM.userRepoBooks.collectAsState(emptyList())
        if(userBooks.isNotEmpty()){
            LazyColumn (Modifier.fillMaxSize()){
                items(userBooks){ book ->
                    BookRecord(book, libMVVM, context)
                }
            }
        }else{
            Column (Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){

                Text("No books found", style = TextStyle(
                        fontSize = 30.sp
                    )
                )
                Text("Check our library for", style = TextStyle(
                    fontSize = 30.sp
                    )
                )
                Text("something interesting", style = TextStyle(
                    fontSize = 30.sp
                    )
                )
            }
        }

    }


@Composable
fun BookRecord(book: BookWithContent, libMVVM: LibraryViewModel, context: Context) {
    val currentUser by libMVVM.currentUser.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp)
            .clickable {
                val fileDir = context.filesDir
                val file = File(fileDir, "book_${book.id}.txt")
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

                file.writeText(book.content!!)

                val intent = Intent(context, BookActivity::class.java)
                    .putExtra("uri", uri.toString())
                    .putExtra("ID" , book.id)
                    .putExtra("title",book.title)
                    .putExtra("author",book.author)
                    .putExtra("scroll",book.scrollState)
                    context.startActivity(intent)
            }
    ) {
        Text(
            text = book.title ?:"NO TITLE",
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
            book.bookshelves?.forEach { shelf ->
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
            book.languages?.forEach { language ->
                GenreTag(genre = language)
            }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    libMVVM.deleteUserBookFromRepo(currentUser!!,book.id!!)
                },
                colors = ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.Transparent
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete, contentDescription = "Delete",
                    tint = Color.Black,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}