package com.threadsdl.service

import com.threadsdl.models.VideoVersion
import java.io.File

/**
 * Main service facade for Threads video operations
 *
 * Coordinates video extraction and downloading by delegating to specialized components:
 * - VideoExtractor: Handles Selenium-based video URL extraction
 * - VideoDownloader: Handles HTTP-based video downloads
 */
class ThreadsService {
    private val extractor = VideoExtractor()
    private val downloader = VideoDownloader()

    /**
     * Extract post ID from a Threads URL
     */
    fun extractPostId(url: String): String? = extractor.extractPostId(url)

    /**
     * Fetch video URLs from a Threads post
     *
     * @param postUrl The Threads post URL
     * @return List of available video versions
     */
    suspend fun fetchVideoUrls(postUrl: String): List<VideoVersion> =
        extractor.fetchVideoUrls(postUrl)

    /**
     * Download a video from URL with progress tracking
     *
     * @param videoUrl The video URL to download
     * @param destinationFile The destination file
     * @param onProgress Callback for download progress (downloaded bytes, total bytes)
     */
    suspend fun downloadVideo(
        videoUrl: String,
        destinationFile: File,
        onProgress: (downloaded: Long, total: Long) -> Unit
    ) = downloader.downloadVideo(videoUrl, destinationFile, onProgress)

    /**
     * Cleanup all resources
     */
    fun cleanup() {
        downloader.cleanup()
    }
}
