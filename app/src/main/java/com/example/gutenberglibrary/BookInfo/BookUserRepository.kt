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
                println("Użytkownik dodany: ${user.uid}")
            } catch (e: Exception) {
                println("Błąd podczas dodawania użytkownika: ${e.message}")
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
                println("Książka dodana dla użytkownika: ${user.uid}")
            } else {
                println("Książka o ID ${book.id} już istnieje w kolekcji.")
            }
        }
        catch (e: Exception) {
            println("Błąd podczas dodawania książki: ${e.message}")
        }
    }
    suspend fun updateScrollState(user: FirebaseUser, book: BookWithContent) {
        try {
            // Query to find the book by its ID
            val querySnapshot = db.collection("users")
                .document(user.uid)
                .collection("books")
                .whereEqualTo("id", book.id)
                .get()
                .await()

            // Check if a book document was found
            if (!querySnapshot.isEmpty) {
                val bookDocument = querySnapshot.documents.first()

                // Log the current scrollState value to verify
                println("Current scrollState: ${book.scrollState}")

                // Perform the update
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
            // Catch and log errors during the update process
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
                println("Książka usunięta: $bookID")
            } else {
                println("Nie znaleziono książki z ID: $bookID")
            }
        }catch (e : Exception){
            println("Błąd podczas usuwania książki: ${e.message}")
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
            println("Błąd podczas pobierania książek: ${e.message}")
            emptyList()
        }
    }

}
