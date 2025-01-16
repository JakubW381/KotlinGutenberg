package com.example.gutenberglibrary.BookService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.gutenberglibrary.BookInfo.BookWithContent
import java.io.File

class BookBroadcastReceiver(private val onDataReceived: (BookWithContent) -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("BookBroadcastReceiver ---->", "Action: ${intent?.action}")
        if (intent?.action == "com.example.gutenberglibrary.BOOK_DOWNLOADED") {
            val contentUri = intent?.getStringExtra("CONTENT_URI")
            val bookId = intent?.getIntExtra("ID", -1) ?: -1
            val downloadMethod = intent?.getStringExtra("DOWNLOAD") ?: "cloud"

            if (contentUri != null && context != null) {
                val uri = Uri.parse(contentUri)
                val inputStream = context.contentResolver.openInputStream(uri)
                val content = inputStream?.bufferedReader().use { it?.readText() ?: "" }

                val bookWithContent = BookWithContent(
                    id = bookId,
                    content = content,
                    author = downloadMethod,
                )
                Log.d("content received in receiver ----->", content)
                deleteFile(uri)
                onDataReceived(bookWithContent)
            }
        }
    }

    private fun deleteFile(uri: Uri) {
        try {
            val file = File(uri.path ?: "")
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