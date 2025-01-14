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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GutenbergLibraryTheme {

                val libraryViewModel: LibraryViewModel by viewModels()
                val currentUser by libraryViewModel.currentUser.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
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

    fun update(){

        val libraryViewModel: LibraryViewModel by viewModels()
        val user = libraryViewModel.currentUser.value
        if (user!!.uid.isNullOrBlank()){
            libraryViewModel.getUserRepoBooks(user!!)
        }
    }
    override fun onResume() {
        //update()
        Log.d("resume main ---->", "resume")
        super.onResume()
    }


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
                Text(headerValue, style = TextStyle(fontSize = 40.sp))
                IconButton(
                    onClick = {
                        AuthManager(context).signOut()
                        libraryViewModel.updateCurrentUser(null)
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp , contentDescription = "Exit",
                    )
                }
            }
            Row (Modifier.fillMaxWidth()){
                Button(
                    onClick = { libraryViewModel.updateScreen(0) },
                    shape = RectangleShape,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Magenta
                    )
                ) { Text("Library") }
                Button(
                    onClick = { libraryViewModel.updateScreen(1) },
                    shape = RectangleShape,
                    modifier = Modifier.weight(1f)
                ) { Text("Cloud") }
                Button(
                    onClick = { libraryViewModel.updateScreen(2) },
                    shape = RectangleShape,
                    modifier = Modifier.weight(1f)
                ) { Text("Storage") }
            }
            if (currentScreen == 0){
                LibraryScreen(libraryViewModel,context)
            }
            if (currentScreen == 1){
                libraryViewModel.getUserRepoBooks(currentUser!!)
                CloudScreen(libraryViewModel,context)
            }
            if (currentScreen == 2){

            }
        }
    }
}
















