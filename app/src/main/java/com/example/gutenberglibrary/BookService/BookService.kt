package com.example.gutenberglibrary.BookService

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

class BookService : Service() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var bookID: Int = 0
    private var downloadMethod: String = "cloud"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            bookID = it.getIntExtra("ID",0)
            downloadMethod = it.getStringExtra("DOWNLOAD") ?: "cloud"
            val url = "https://www.gutenberg.org/cache/epub/${bookID}/pg${bookID}.txt"
            Log.d("URL ----->",url)
            download(url)
        }
        return START_STICKY
    }

    private suspend fun getBookHttp(url: String): String = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Error: ${response.code}")
                }
                response.body?.string().orEmpty()
            }
        } catch (e: Exception) {
            "Exception: ${e.message}"
        }
    }
    private fun download(url: String) {
        coroutineScope.launch {
            try {
                val bookContent = getBookHttp(url)
                val file = File(filesDir, "book_$bookID.txt")
                file.writeText(bookContent)

                val uri = FileProvider.getUriForFile(applicationContext, "${applicationContext.packageName}.provider", file)
                val broadcastIntent = Intent("com.example.gutenberglibrary.BOOK_DOWNLOADED").apply {
                    putExtra("CONTENT_URI", uri.toString())
                    putExtra("ID", bookID)
                    putExtra("DOWNLOAD" , downloadMethod)
                }
                Log.d("sending intent", "sending intent")
                sendBroadcast(broadcastIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                stopSelf()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }
}
