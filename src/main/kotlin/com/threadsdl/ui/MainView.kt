package com.threadsdl.ui

import com.threadsdl.models.VideoVersion
import com.threadsdl.service.ThreadsServiceNew
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.ChoiceDialog
import javafx.stage.FileChooser
import kotlinx.coroutines.*
import tornadofx.*
import java.text.DecimalFormat

class MainView : View("Threads Video Downloader") {
    private val service = ThreadsServiceNew()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var urlInput by property("")
    fun urlInputProperty() = getProperty(MainView::urlInput)

    private var statusText by property("Ready")
    fun statusTextProperty() = getProperty(MainView::statusText)

    private var progressValue by property(0.0)
    fun progressValueProperty() = getProperty(MainView::progressValue)

    private var isDownloading by property(false)
    fun isDownloadingProperty() = getProperty(MainView::isDownloading)

    private var availableVideos = mutableListOf<VideoVersion>()

    override val root = vbox(spacing = 15) {
        padding = insets(20)
        prefWidth = 600.0
        prefHeight = 400.0

        label("Threads Video Downloader") {
            style {
                fontSize = 24.px
                fontWeight = javafx.scene.text.FontWeight.BOLD
            }
        }

        form {
            fieldset("Enter Threads URL") {
                field("URL:") {
                    textfield(urlInputProperty()) {
                        promptText = "https://www.threads.net/@username/post/..."
                        prefWidth = 450.0
                    }
                }
            }
        }

        hbox(spacing = 10) {
            alignment = Pos.CENTER_LEFT

            button("Fetch Videos") {
                disableProperty().bind(isDownloadingProperty())
                action {
                    fetchVideos()
                }
            }

            button("Download Best Quality") {
                disableProperty().bind(isDownloadingProperty())
                action {
                    downloadBestQuality()
                }
            }

            button("Choose Quality & Download") {
                disableProperty().bind(isDownloadingProperty())
                action {
                    chooseQualityAndDownload()
                }
            }
        }

        separator()

        label("Status:") {
            style {
                fontWeight = javafx.scene.text.FontWeight.BOLD
            }
        }

        label(statusTextProperty()) {
            style {
                fontSize = 12.px
            }
        }

        progressbar {
            progressProperty().bind(progressValueProperty())
            prefWidth = 560.0
        }

        separator()

        label("Available Videos:") {
            style {
                fontWeight = javafx.scene.text.FontWeight.BOLD
            }
        }

        textarea {
            isEditable = false
            prefHeight = 100.0
            id = "videoList"
        }
    }

    private fun fetchVideos() {
        val url = urlInput.trim()
        if (url.isEmpty()) {
            showError("Please enter a Threads URL")
            return
        }

        val postId = service.extractPostId(url)
        if (postId == null) {
            showError("Invalid Threads URL")
            return
        }

        isDownloading = true
        statusText = "Fetching video information..."
        progressValue = 0.0

        coroutineScope.launch {
            try {
                availableVideos.clear()
                // Pass the full URL instead of just the post ID
                val videos = service.fetchVideoUrls(url)
                availableVideos.addAll(videos)

                val videoListText = videos.mapIndexed { index, video ->
                    "${index + 1}. ${video.width}x${video.height} (Type: ${video.type ?: "N/A"})"
                }.joinToString("\n")

                Platform.runLater {
                    root.lookup("#videoList")?.let { node ->
                        (node as? javafx.scene.control.TextArea)?.text = videoListText
                    }
                }

                statusText = "Found ${videos.size} video(s). Ready to download."
            } catch (e: Exception) {
                statusText = "Error: ${e.message}"
                showError("Failed to fetch videos: ${e.message}")
            } finally {
                isDownloading = false
                progressValue = 0.0
            }
        }
    }

    private fun downloadBestQuality() {
        if (availableVideos.isEmpty()) {
            showError("Please fetch videos first")
            return
        }

        // Select the highest quality video (largest width)
        val bestVideo = availableVideos.maxByOrNull { it.width ?: 0 }
        if (bestVideo != null) {
            downloadVideo(bestVideo)
        }
    }

    private fun chooseQualityAndDownload() {
        if (availableVideos.isEmpty()) {
            showError("Please fetch videos first")
            return
        }

        val choices = availableVideos.mapIndexed { index, video ->
            "${index + 1}. ${video.width}x${video.height}"
        }

        val dialog = ChoiceDialog(choices.first(), choices)
        dialog.title = "Select Video Quality"
        dialog.headerText = "Choose a quality:"
        dialog.contentText = "Quality:"

        dialog.showAndWait().ifPresent { choice ->
            val index = choices.indexOf(choice)
            if (index >= 0) {
                downloadVideo(availableVideos[index])
            }
        }
    }

    private fun downloadVideo(video: VideoVersion) {
        val fileChooser = FileChooser()
        fileChooser.title = "Save Video"
        fileChooser.initialFileName = "threads_video_${video.width}x${video.height}.mp4"
        fileChooser.extensionFilters.add(
            FileChooser.ExtensionFilter("MP4 Video", "*.mp4")
        )

        val file = fileChooser.showSaveDialog(currentStage)
        if (file == null) {
            return
        }

        isDownloading = true
        statusText = "Downloading video..."
        progressValue = 0.0

        coroutineScope.launch {
            try {
                service.downloadVideo(video.url, file) { downloaded, total ->
                    Platform.runLater {
                        if (total > 0) {
                            progressValue = downloaded.toDouble() / total.toDouble()
                            val downloadedMB = downloaded / (1024.0 * 1024.0)
                            val totalMB = total / (1024.0 * 1024.0)
                            val format = DecimalFormat("#.##")
                            statusText = "Downloading: ${format.format(downloadedMB)} MB / ${format.format(totalMB)} MB"
                        } else {
                            statusText = "Downloading: ${downloaded / (1024 * 1024)} MB / Unknown size"
                        }
                    }
                }

                statusText = "Download completed: ${file.absolutePath}"
                progressValue = 1.0
                showInfo("Download completed successfully!")
            } catch (e: Exception) {
                statusText = "Error: ${e.message}"
                showError("Failed to download video: ${e.message}")
            } finally {
                isDownloading = false
            }
        }
    }

    private fun showError(message: String) {
        Platform.runLater {
            Alert(Alert.AlertType.ERROR).apply {
                title = "Error"
                headerText = null
                contentText = message
                showAndWait()
            }
        }
    }

    private fun showInfo(message: String) {
        Platform.runLater {
            Alert(Alert.AlertType.INFORMATION).apply {
                title = "Success"
                headerText = null
                contentText = message
                showAndWait()
            }
        }
    }

    override fun onUndock() {
        super.onUndock()
        coroutineScope.cancel()
    }
}
