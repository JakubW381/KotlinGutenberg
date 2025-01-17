package com.example.gutenberglibrary

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gutenberglibrary.AppScreens.CloudScreen
import com.example.gutenberglibrary.AppScreens.LibraryScreen
import com.example.gutenberglibrary.Auth.AuthManager
import com.example.gutenberglibrary.Auth.LoginScreen
import com.example.gutenberglibrary.ui.theme.GutenbergLibraryTheme
import kotlinx.coroutines.launch


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

                createNotificationChannel()

                Scaffold(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondary),
                    containerColor = MaterialTheme.colorScheme.secondary,

                ) { innerPadding ->
                    if (currentUser == null) {
                        LoginScreen(
                            modifier = Modifier.padding(innerPadding),
                            context = this,
                            updateVM = { user -> libraryViewModel.updateCurrentUser(user) }
                        )
                    } else {
                        AppContainer(Modifier.padding(innerPadding), context, libraryViewModel)
                        libraryViewModel.downloadBooksFromApi(1,"","en,pl,fr,de,ru","")
                    }
                }
            }
        }
    }
    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val name = "GutenbergLibrary"
            val descriptionText = "Book Saved"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("default_channel",name,importance).apply {
                description = descriptionText
            }
            val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun AppContainer(modifier: Modifier = Modifier, context: Context, libraryViewModel: LibraryViewModel) {
        val currentScreen by libraryViewModel.currentScreen.collectAsState()
        val currentUser by libraryViewModel.currentUser.collectAsState()
        var headerValue by remember { mutableStateOf("") }

        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()



        ModalNavigationDrawer(
            gesturesEnabled = true,
            drawerContent = {
                ModalDrawerSheet (
                    modifier = Modifier.width(250.dp),
                    drawerContainerColor = MaterialTheme.colorScheme.tertiary,
                    drawerContentColor = MaterialTheme.colorScheme.surface,
                ){
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                            .background(MaterialTheme.colorScheme.tertiary)
                        , horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(R.drawable.lib), contentDescription = "logo",modifier = Modifier.padding(top = 20.dp, bottom = 10.dp).size(100.dp).background(MaterialTheme.colorScheme.outline, shape = CircleShape))
                        Text(currentUser!!.email!!, style = TextStyle(color = MaterialTheme.colorScheme.outline, fontSize = 15.sp))
                        HorizontalDivider(thickness = 2.dp,color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(vertical = 10.dp))


                        NavigationDrawerItem(
                            label = { Text("Sign Out" ,style = TextStyle(color = Color(0xFF8B0000)))},
                            selected = false,
                            icon = { Icon(Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = null, tint = Color(
                                0xFF8B0000
                            )
                            ) },
                            onClick = {
                                AuthManager(context).signOut()
                                libraryViewModel.updateCurrentUser(null)
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = MaterialTheme.colorScheme.primary

                            )
                        )

                        Spacer(Modifier.height(12.dp))
                    }
                }
            },
            drawerState = drawerState
        ) {


            Column (modifier.fillMaxSize()){
                Row (
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    if (currentScreen == 0) headerValue = "Library"
                    if (currentScreen == 1) headerValue = "Cloud"
                    Text(headerValue, style = TextStyle(fontSize = 40.sp, color = MaterialTheme.colorScheme.surface))

                    IconButton(
                        onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if(isClosed) open() else close()
                                }
                            }
                        },
                    ) {
                        Icon(Icons.Default.Menu , contentDescription = "Exit", tint = MaterialTheme.colorScheme.surface)
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

                }
                if (currentScreen == 0){
                    LibraryScreen(libraryViewModel,context)
                }
                if (currentScreen == 1){
                    libraryViewModel.getUserRepoBooks(currentUser!!)
                    CloudScreen(libraryViewModel,context)
                }
            }
        }
    }
}
















