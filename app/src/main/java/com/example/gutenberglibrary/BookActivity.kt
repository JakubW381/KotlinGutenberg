package com.example.gutenberglibrary

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gutenberglibrary.BookInfo.BookUserRepository
import com.example.gutenberglibrary.BookInfo.BookWithContent
import com.example.gutenberglibrary.ui.theme.GutenbergLibraryTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

class BookActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentUriString = intent.getStringExtra("uri")
        val bookID  = intent.getIntExtra("ID", 0)
        val bookTitle = intent.getStringExtra("title")
        val bookAuthor = intent.getStringExtra("author")
        val scrollState = intent.getIntExtra("scroll",0)
        val contentUri = Uri.parse(contentUriString)

        var content : String

            try {
                val inputStream: InputStream? = contentUri?.let {
                    contentResolver.openInputStream(it)
                }
                content = inputStream?.bufferedReader().use { it?.readText() ?: "Failed to load content" }
            } catch (e: Exception) {
                e.printStackTrace()
                content = "Error reading content"
            }

        val bookWithContent = BookWithContent(
            bookID,bookTitle,bookAuthor,
            content = content,
            scrollState = scrollState
        )

        enableEdgeToEdge()
        setContent {
            GutenbergLibraryTheme {
                Scaffold { innerPadding ->
                        BookScreen(Modifier.padding(innerPadding), bookWithContent){bookWithContent ->
                            repoFun(bookWithContent)}
                }
            }
        }
    }
    @Composable
    fun BookScreen(modifier : Modifier = Modifier, bookWithContent: BookWithContent,scrollStateUpdate : (bookWithContent: BookWithContent) -> Unit){
        val scrollState = rememberScrollState( initial = bookWithContent.scrollState?: 0)
        var fontSize by remember { mutableIntStateOf(16) }

        Box(
            modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)
        ){
            Column (
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ){
                Row (horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.tertiary)
                ){
                    Column (
                        Modifier
                            .weight(4f)
                            .heightIn(100.dp)
                            ){
                        Text(bookWithContent.title!! , style = TextStyle(
                                fontSize = 20.sp
                            ),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 8.dp,start = 10.dp)
                        )
                        Text(bookWithContent.author!! , style = TextStyle(
                                fontSize = 15.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                    Button(
                        onClick = {
                            val coroutineScope = CoroutineScope(Dispatchers.IO)

                            bookWithContent.scrollState = scrollState.value
                            coroutineScope.launch {
                                try {
                                    scrollStateUpdate(bookWithContent)
                                } catch (e: Exception) {
                                    Log.e("BookActivity", "Error updating scroll state", e)
                                } finally {
                                    finish()
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        colors = ButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = Color.Transparent
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Exit",
                            tint = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
            Column (
                Modifier
                    .padding(50.dp)
                    .padding(top = 60.dp)
                    .verticalScroll(scrollState)
                    .background(MaterialTheme.colorScheme.primary)
            ){
                Text(bookWithContent.content!!, style = TextStyle(color = MaterialTheme.colorScheme.surface, fontSize = fontSize.sp))
            }
            Column(Modifier.align(Alignment.BottomEnd).padding(20.dp).background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(100.dp)), horizontalAlignment = Alignment.CenterHorizontally){
                IconButton(
                    onClick = {if (fontSize < 32) fontSize += 1 }
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Plus")
                }
                Text("$fontSize")
                IconButton(
                    onClick = {if (fontSize > 12) fontSize -= 1 }
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Minus")
                }
            }
        }
    }
    private fun repoFun(bookWithContent: BookWithContent){
        val coroutineScope  = CoroutineScope(Dispatchers.IO)
        val repo = BookUserRepository()
        val currentUser = Firebase.auth.currentUser
        coroutineScope.launch {
            repo.updateScrollState(currentUser!!, bookWithContent)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        try {
            val contentUriString = intent.getStringExtra("uri")
            val contentUri = Uri.parse(contentUriString)
            val file = File(contentUri.path ?: "")
            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    Log.d("BookBroadcastReceiver", "File deleted successfully")
                } else {
                    Log.d("BookBroadcastReceiver", "Failed to delete the file")
                }
            }
        } catch (e: Exception) {
            Log.e("BookBroadcastReceiver", "Error deleting file", e)
        }
    }
}
