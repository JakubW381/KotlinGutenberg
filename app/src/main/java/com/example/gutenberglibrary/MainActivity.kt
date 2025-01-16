package com.example.gutenberglibrary

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.gutenberglibrary.AppScreens.CloudScreen
import com.example.gutenberglibrary.AppScreens.LibraryScreen
import com.example.gutenberglibrary.Auth.AuthManager
import com.example.gutenberglibrary.Auth.LoginScreen
import com.example.gutenberglibrary.ui.theme.GutenbergLibraryTheme


class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val context : Context = this

//        var bookDB: BookDB = Room.databaseBuilder(
//                        context.applicationContext,
//                        BookDB::class.java,
//                        "book_db"
//                    ).build()


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GutenbergLibraryTheme {

                val libraryViewModel: LibraryViewModel by viewModels()
                val currentUser by libraryViewModel.currentUser.collectAsState()
                //libraryViewModel.initRoomDatabase(bookDB)

                Scaffold(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondary),
                    containerColor = MaterialTheme.colorScheme.secondary,

                ) { innerPadding ->
                    if (currentUser == null) {
                        LoginScreen(
                            modifier = Modifier.padding(innerPadding),
                            context = this,
                            email = "",
                            password = "",
                            updateVM = { user -> libraryViewModel.updateCurrentUser(user) }
                        )
                    } else {
                        libraryViewModel.downloadBooksFromApi(1,"","en,pl,fr,de,ru","")
                        AppContainer(Modifier.padding(innerPadding), context, libraryViewModel)
                    }
                }
            }
        }
    }

//    fun update(){
//        val libraryViewModel: LibraryViewModel by viewModels()
//        val user = libraryViewModel.currentUser.value
//        if (user?.uid.isNullOrBlank()){
//            libraryViewModel.getUserRepoBooks(user!!)
//        }
//    }
//    override fun onResume() {
//        super.onResume()
//        //update()
//        Log.d("resume main ---->", "resume")
//    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun AppContainer(modifier: Modifier = Modifier, context: Context, libraryViewModel: LibraryViewModel) {
        val currentScreen by libraryViewModel.currentScreen.collectAsState()
        val currentUser by libraryViewModel.currentUser.collectAsState()
        var headerValue by remember { mutableStateOf("") }
        Column (modifier.fillMaxSize()){
            Row (
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
                ){
                if (currentScreen == 0) headerValue = "Library"
                if (currentScreen == 1) headerValue = "Cloud"
                if (currentScreen == 2) headerValue = "Storage"
                Text(headerValue, style = TextStyle(fontSize = 40.sp, color = MaterialTheme.colorScheme.surface))
                IconButton(
                    onClick = {
                        AuthManager(context).signOut()
                        libraryViewModel.updateCurrentUser(null)
                    },
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp , contentDescription = "Exit", tint = MaterialTheme.colorScheme.surface)
                }
            }
            Row (Modifier.fillMaxWidth()){
                Button(
                    onClick = { libraryViewModel.updateScreen(0) },
                    shape = RectangleShape,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(currentScreen == 0)
                        {MaterialTheme.colorScheme.outline} else {
                            MaterialTheme.colorScheme.tertiary
                        },
                        contentColor = MaterialTheme.colorScheme.surface
                    )
                ) { Text("Library") }
                Button(
                    onClick = { libraryViewModel.updateScreen(1) },
                    shape = RectangleShape,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(currentScreen == 1)
                        {MaterialTheme.colorScheme.outline} else {
                            MaterialTheme.colorScheme.tertiary
                        },
                        contentColor = MaterialTheme.colorScheme.surface
                    )
                ) { Text("Cloud") }
//                Button(
//                    onClick = { libraryViewModel.updateScreen(2) },
//                    shape = RectangleShape,
//                    modifier = Modifier.weight(1f),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = if(currentScreen == 2)
//                        {MaterialTheme.colorScheme.outline} else {
//                            MaterialTheme.colorScheme.tertiary
//                        },
//                        contentColor = MaterialTheme.colorScheme.surface
//                    )
//                ) { Text("Storage") }
            }
            if (currentScreen == 0){
                LibraryScreen(libraryViewModel,context)
            }
            if (currentScreen == 1){
                libraryViewModel.getUserRepoBooks(currentUser!!)
                CloudScreen(libraryViewModel,context)
            }
//            if (currentScreen == 2){
//                libraryViewModel.getStorageBooks()
//                StorageScreen(libraryViewModel,context)
//            }
        }
    }
}
















