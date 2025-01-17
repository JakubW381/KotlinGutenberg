package com.example.gutenberglibrary.Auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.firebase.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.security.MessageDigest
import java.util.UUID
import androidx.credentials.CustomCredential
import com.example.gutenberglibrary.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.*
import kotlinx.coroutines.channels.awaitClose


class AuthManager (val context: Context){


    private val auth = Firebase.auth

    fun createAccountEmailPassword(email : String , pass : String) : Flow<AuthResponse> = callbackFlow {
        auth.createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    trySend(AuthResponse.Success)
                }else{
                    trySend(
                        AuthResponse.Error(
                            message = task.exception?.message ?: "Unknown Error"
                        )
                    )
                }
            }
        awaitClose()
    }

    fun loginWithEmail(email : String, pass : String) : Flow<AuthResponse> = callbackFlow {
        auth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(AuthResponse.Success)
                }else{
                    trySend(
                        AuthResponse.Error(
                            message = task.exception?.message ?: "Unknown Error"
                        )
                    )
                }
            }
        awaitClose()
    }
    fun signOut(){
        auth.signOut()
    }

    fun createNonce(): String{
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return digest.fold(""){ str, it ->
            str + "%02x".format(it)

        }
    }

    fun signInWithGoogle() : Flow<AuthResponse> = callbackFlow {

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.web_client_id))
            .setAutoSelectEnabled(false)
            .setNonce(createNonce())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try{
            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(context,request)

            val credential = result.credential
            if (credential is CustomCredential){
                if(credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                        val firebaseCredential = GoogleAuthProvider
                            .getCredential(
                                googleIdTokenCredential.idToken,
                                null
                            )

                        auth.signInWithCredential(firebaseCredential).addOnCompleteListener{
                            if (it.isSuccessful){
                                trySend(AuthResponse.Success)
                            }else{
                                trySend(
                                    AuthResponse.Error(
                                        message = it.exception?.message ?: "Unknown Error"
                                    )
                                )
                            }
                        }

                    }catch (e : GoogleIdTokenParsingException){
                        trySend(AuthResponse.Error(message = e.localizedMessage))
                    }
                }
            }
        }catch (e : Exception){
            trySend(AuthResponse.Error(message = e.localizedMessage))
        }
        awaitClose()
    }
}



























