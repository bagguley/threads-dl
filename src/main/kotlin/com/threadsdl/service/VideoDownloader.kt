package com.threadsdl.service

import com.threadsdl.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

/**
 * Handles video downloads from URLs with progress tracking
 */
class VideoDownloader {
    private val client = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    /**
     * Download a video from URL with progress callback
     *
     * @param videoUrl The video URL to download
     * @param destinationFile The file to save to
     * @param onProgress Callback for download progress
     */
    suspend fun downloadVideo(
        videoUrl: String,
        destinationFile: File,
        onProgress: (downloaded: Long, total: Long) -> Unit
    ) = withContext(Dispatchers.IO) {
        require(videoUrl.isNotBlank()) { "Video URL cannot be blank" }
        require(videoUrl.startsWith("http", ignoreCase = true)) { "Invalid video URL format" }

        // Ensure parent directory exists
        destinationFile.parentFile?.mkdirs()

        val request = Request.Builder()
            .url(videoUrl)
            .header("User-Agent", Constants.USER_AGENT)
            .header("Referer", Constants.THREADS_BASE_URL)
            .build()

        client.newCall(request).execute().use { response ->
            check(response.isSuccessful) { "${Constants.ERR_DOWNLOAD_FAILED} ${response.code}" }

            val body = checkNotNull(response.body) { "Empty response body" }
            val contentLength = body.contentLength()

            body.byteStream().use { inputStream ->
                destinationFile.outputStream().buffered().use { outputStream ->
                    val buffer = ByteArray(Constants.BUFFER_SIZE)
                    var bytesRead: Int
                    var totalBytesRead = 0L

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        onProgress(totalBytesRead, contentLength)
                    }
                }
            }
        }
    }

    /**
     * Cleanup resources
     */
    fun cleanup() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }
}
