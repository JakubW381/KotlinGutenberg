package com.example.gutenberglibrary.BookInfo

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BookUserRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun addUser(user: FirebaseUser?) {
        if (user != null) {
            try {
                val firestoreUser = UserInfo(
                    id = user.uid,
                    email = user.email ?: ""
                )
                db.collection("users")
                    .document(user.uid)
                    .set(firestoreUser)
                    .await()
                println("User added: ${user.uid}")
            } catch (e: Exception) {
                println("Error adding user: ${e.message}")
            }
        }
    }
    suspend fun addUserBook(user: FirebaseUser, book: BookWithContent) {
        try {
            val querySnapshot = db.collection("users")
                .document(user.uid)
                .collection("books")
                .whereEqualTo("id", book.id)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                db.collection("users")
                    .document(user.uid)
                    .collection("books")
                    .add(book)
                    .await()
                println("Book added to user:${user.uid} repo: ")
            } else {
                println("Book: ${book.id} already exists in user: ${user.uid} repo")
            }
        }
        catch (e: Exception) {
            println("Error adding book: ${e.message}")
        }
    }
    suspend fun updateScrollState(user: FirebaseUser, book: BookWithContent) {
        try {
            val querySnapshot = db.collection("users")
                .document(user.uid)
                .collection("books")
                .whereEqualTo("id", book.id)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val bookDocument = querySnapshot.documents.first()

                db.collection("users")
                    .document(user.uid)
                    .collection("books")
                    .document(bookDocument.id)
                    .update("scrollState", book.scrollState)
                    .await()

                println("ScrollStateUpdated: ${user.uid}, Book ID: ${book.id}, scrollState: ${book.scrollState}")
            } else {
                println("No book found with ID: ${book.id} for user: ${user.uid}")
            }
        } catch (e: Exception) {
            println("Error updating scrollState: ${e.message}")
            e.printStackTrace()
        }
    }
    suspend fun deleteUserBook(user:FirebaseUser , bookID : Int){
        try {
            val querySnapshot = db.collection("users")
                .document(user.uid)
                .collection("books")
                .whereEqualTo("id", bookID)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentToDelete = querySnapshot.documents.first()
                documentToDelete.reference.delete().await()
                println("Book Deleted: $bookID")
            } else {
                println("No book found with ID: $bookID")
            }
        }catch (e : Exception){
            println("Error deleting book: ${e.message}")
        }
    }
    suspend fun getUserBooks(user:FirebaseUser): List<BookWithContent> {
        return try {
            val snapshot = db.collection("users")
                .document(user.uid)
                .collection("books")
                .get()
                .await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(BookWithContent::class.java)
            }
        } catch (e: Exception) {
            println("Error fetching books: ${e.message}")
            emptyList()
        }
    }

}
