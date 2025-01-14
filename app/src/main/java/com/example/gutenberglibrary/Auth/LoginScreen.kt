package com.example.gutenberglibrary.Auth

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gutenberglibrary.BookInfo.BookUserRepository
import com.example.gutenberglibrary.MainActivity
import com.example.gutenberglibrary.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(modifier : Modifier = Modifier, context : Context, email: String, password: String, updateVM: (FirebaseUser?) -> Unit){

    var emailValue by remember{ mutableStateOf("") }
    var passwordValue by remember{ mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val bookUserRepository = BookUserRepository()

    Column (modifier.fillMaxSize().background(Color.White)){

        Column (modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
            Text("Welcome to",
                style = TextStyle(
                    color = Color.Black,              // kolor tekstu
                    fontSize = 30.sp,                 // rozmiar czcionki
                    fontWeight = FontWeight.Normal,     // pogrubienie
                    letterSpacing = 1.5.sp,          // odstęp między literami
                    lineHeight = 24.sp,               // wysokość linii
                )
            )
            Text("ProjectGutenberg x Gutendex API",
                style = TextStyle(
                    color = Color.Black,              // kolor tekstu
                    fontSize = 25.sp,                 // rozmiar czcionki
                    fontWeight = FontWeight.W200,     // pogrubienie
                    letterSpacing = 1.5.sp,          // odstęp między literami
                    lineHeight = 24.sp,               // wysokość linii
                )
            )
            Text("by Jakub Wołowiec",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 1.5.sp,
                    lineHeight = 24.sp,
                )
            )
        }

        TextField(modifier = Modifier.align(Alignment.CenterHorizontally), value = emailValue, onValueChange = {emailValue = it}, label = {Text("Email")})
        TextField(modifier = Modifier.align(Alignment.CenterHorizontally), value = passwordValue, onValueChange = {passwordValue = it}, label = {Text("Password")})
        Row (modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center){
            Button(
                onClick = {
                    if (emailValue.isBlank() || passwordValue.isBlank()) {
                        Toast.makeText(context, "Please fill in both email and password", Toast.LENGTH_SHORT).show()
                    }

                    AuthManager(context).loginWithEmail(emailValue, passwordValue)
                        .onEach { response ->
                            if (response is AuthResponse.Success) {
                                coroutineScope.launch {
                                    updateVM(Firebase.auth.currentUser)
                                    context.startActivity(Intent(context, MainActivity::class.java))
                                }
                            } else {
                                Toast.makeText(context, (response as AuthResponse.Error).message, Toast.LENGTH_SHORT).show()
                            }
                        }
                        .launchIn(coroutineScope)
                },
                modifier = modifier
            ) {
                Text("Login")
            }
            Button(
                onClick = {
                    if (emailValue.isBlank() || passwordValue.isBlank()) {
                        Toast.makeText(context, "Please fill in both email and password", Toast.LENGTH_SHORT).show()
                    }

                    AuthManager(context).createAccountEmailPassword(emailValue, passwordValue)
                        .onEach { response ->
                            if (response is AuthResponse.Success) {
                                coroutineScope.launch {
                                    updateVM(Firebase.auth.currentUser)
                                    bookUserRepository.addUser(Firebase.auth.currentUser)
                                    context.startActivity(Intent(context, MainActivity::class.java))
                                }
                            } else {
                                Toast.makeText(context, (response as AuthResponse.Error).message, Toast.LENGTH_SHORT).show()
                            }
                        }.launchIn(coroutineScope)
                },
                modifier = modifier
            ) {
                Text("Register")
            }
        }
        Text(text = "or continue with" , modifier = Modifier.align(Alignment.CenterHorizontally))
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally).size(125.dp).padding(10.dp),
            onClick = {
                AuthManager(context).signInWithGoogle()
                    .onEach {
                        if (it is AuthResponse.Success){
                            coroutineScope.launch {
                                updateVM(Firebase.auth.currentUser)
                                bookUserRepository.addUser(Firebase.auth.currentUser)
                                context.startActivity(Intent(context, MainActivity::class.java))
                            }
                        }else{
                            Toast.makeText(context, (it as AuthResponse.Error).message, Toast.LENGTH_SHORT).show()
                        }
                    }.launchIn(coroutineScope)
            },
            colors = ButtonColors(
                containerColor = Color.White,
                contentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Transparent),
            elevation = ButtonDefaults.elevatedButtonElevation(10.dp)
            ) {
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google Icon"
            )
        }
    }
}



























