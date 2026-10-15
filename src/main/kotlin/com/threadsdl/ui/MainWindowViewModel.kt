package com.threadsdl.ui

import androidx.compose.runtime.*
import com.threadsdl.models.VideoVersion
import com.threadsdl.service.ThreadsService
import com.threadsdl.utils.Constants
import com.threadsdl.utils.formatAsMegabytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

class MainWindowViewModel {
    private val service = ThreadsService()

    var urlInput by mutableStateOf("")
        private set

    var statusText by mutableStateOf("Ready")
        private set

    var progressValue by mutableStateOf(0f)
        private set

    var isDownloading by mutableStateOf(false)
        private set

    var availableVideos by mutableStateOf<List<VideoVersion>>(emptyList())
        private set

    var videoListText by mutableStateOf("")
        private set

    var showErrorDialog by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf("")
        private set

    var showSuccessDialog by mutableStateOf(false)
        private set

    var successMessage by mutableStateOf("")
        private set

    var hasDetectedVideo by mutableStateOf(false)
        private set

    fun onUrlInputChange(newUrl: String) {
        urlInput = newUrl
        hasDetectedVideo = false
        availableVideos = emptyList()
        videoListText = ""
    }

    fun detectVideo(scope: CoroutineScope) {
        val url = urlInput.trim()

        validateThreadsUrl(url)?.let { error ->
            errorMessage = error
            showErrorDialog = true
            return
        }

        isDownloading = true
        statusText = Constants.MSG_DETECTING
        progressValue = 0f

        scope.launch {
            try {
                val videos = service.fetchVideoUrls(url)
                availableVideos = videos

                videoListText = if (videos.isNotEmpty()) {
                    "${Constants.MSG_VIDEO_READY}\n${videos[0].url.take(Constants.URL_PREVIEW_LENGTH)}..."
                } else {
                    Constants.MSG_NO_VIDEO_FOUND
                }

                statusText = if (videos.isNotEmpty()) {
                    Constants.MSG_FOUND_MAIN_VIDEO
                } else {
                    Constants.MSG_NO_VIDEO_FOUND
                }

                hasDetectedVideo = videos.isNotEmpty()
            } catch (e: Exception) {
                val errorMsg = buildErrorMessage(
                    Constants.ERR_FAILED_TO_FETCH,
                    e
                )
                statusText = "Error: ${e.message}"
                errorMessage = errorMsg
                showErrorDialog = true
                hasDetectedVideo = false
            } finally {
                isDownloading = false
                progressValue = 0f
            }
        }
    }

    fun downloadVideo(file: File, scope: CoroutineScope) {
        if (availableVideos.isEmpty()) {
            errorMessage = Constants.ERR_PLEASE_DETECT_FIRST
            showErrorDialog = true
            return
        }

        val video = availableVideos.first()

        isDownloading = true
        statusText = Constants.MSG_DOWNLOADING
        progressValue = 0f

        scope.launch {
            try {
                service.downloadVideo(video.url, file) { downloaded, total ->
                    if (total > 0) {
                        progressValue = downloaded.toFloat() / total.toFloat()
                        statusText = "Downloading: ${downloaded.formatAsMegabytes()} MB / ${total.formatAsMegabytes()} MB"
                    } else {
                        statusText = "Downloading: ${downloaded.formatAsMegabytes()} MB / Unknown size"
                    }
                }

                statusText = "${Constants.MSG_DOWNLOAD_COMPLETE} ${file.absolutePath}"
                progressValue = 1f
                successMessage = Constants.MSG_DOWNLOAD_COMPLETE
                showSuccessDialog = true
            } catch (e: Exception) {
                val errorMsg = buildDownloadErrorMessage(
                    Constants.ERR_FAILED_TO_DOWNLOAD,
                    e
                )
                statusText = "Error: ${e.message}"
                errorMessage = errorMsg
                showErrorDialog = true
            } finally {
                isDownloading = false
            }
        }
    }

    fun dismissErrorDialog() {
        showErrorDialog = false
    }

    fun dismissSuccessDialog() {
        showSuccessDialog = false
    }

    fun cleanup() {
        service.cleanup()
    }

    private fun validateThreadsUrl(url: String): String? {
        val trimmed = url.trim()

        if (trimmed.isEmpty()) {
            return Constants.ERR_ENTER_URL
        }

        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://") && !trimmed.contains("threads.")) {
            return Constants.ERR_INVALID_URL_FORMAT
        }

        if (service.extractPostId(trimmed) == null) {
            return Constants.ERR_INVALID_URL
        }

        return null
    }

    private fun buildErrorMessage(prefix: String, e: Exception): String {
        return buildString {
            append(prefix)
            append(" ")
            append(e.message ?: "Unknown error")

            when {
                e.message?.contains("timeout", ignoreCase = true) == true ->
                    append("\n\nThe request timed out. Please check your internet connection and try again.")
                e.message?.contains("login", ignoreCase = true) == true ||
                        e.message?.contains("private", ignoreCase = true) == true ->
                    append("\n\nThis post may be private or require login.")
                e is IllegalStateException ->
                    append("\n\nPlease verify the post contains a video and is publicly accessible.")
            }
        }
    }

    private fun buildDownloadErrorMessage(prefix: String, e: Exception): String {
        return buildString {
            append(prefix)
            append(" ")
            append(e.message ?: "Unknown error")

            when {
                e.message?.contains("403", ignoreCase = true) == true ||
                        e.message?.contains("401", ignoreCase = true) == true ->
                    append("\n\nAccess denied. The video URL may have expired or requires authentication.")
                e.message?.contains("404", ignoreCase = true) == true ->
                    append("\n\nVideo not found. The video may have been deleted or the URL is invalid.")
                e.message?.contains("timeout", ignoreCase = true) == true ->
                    append("\n\nThe download timed out. Please check your internet connection and try again.")
                e.message?.contains("space", ignoreCase = true) == true ->
                    append("\n\nPlease ensure you have enough disk space.")
                else ->
                    append("\n\nPlease try detecting the video again and retrying the download.")
            }
        }
    }
}
