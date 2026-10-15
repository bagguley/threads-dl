package com.threadsdl.service

import com.threadsdl.models.VideoVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.logging.LoggingPreferences
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.File
import java.io.IOException
import java.time.Duration
import java.util.logging.Level

class ThreadsServiceNew {
    private val client = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    /**
     * Extract post ID from a Threads URL
     */
    fun extractPostId(url: String): String? {
        val patterns = listOf(
            Regex("""threads\.(net|com)/@[^/]+/post/([A-Za-z0-9_-]+)"""),
            Regex("""threads\.(net|com)/t/([A-Za-z0-9_-]+)"""),
            Regex("""threads\.(net|com).*post/([A-Za-z0-9_-]+)""")
        )

        for (pattern in patterns) {
            val match = pattern.find(url)
            if (match != null) {
                return match.groupValues.last()
            }
        }

        return null
    }

    /**
     * Fetch video URLs using headless Chrome
     */
    suspend fun fetchVideoUrls(postUrl: String): List<VideoVersion> = withContext(Dispatchers.IO) {
        println("=== Using Selenium to load Threads page ===")

        // Configure Chrome options for headless mode
        val options = ChromeOptions()
        options.addArguments("--headless=new")
        options.addArguments("--no-sandbox")
        options.addArguments("--disable-dev-shm-usage")
        options.addArguments("--disable-gpu")
        options.addArguments("--window-size=1920,1080")
        options.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")

        // Enable performance logging to capture network requests
        val logPrefs = LoggingPreferences()
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL)
        options.setCapability("goog:loggingPrefs", logPrefs)

        val driver = ChromeDriver(options)

        try {
            val normalizedUrl = if (postUrl.startsWith("http")) postUrl else "https://www.threads.net/t/$postUrl"
            println("Loading URL: $normalizedUrl")

            driver.get(normalizedUrl)

            // Wait for the page to load
            val wait = WebDriverWait(driver, Duration.ofSeconds(15))

            // Wait for either video elements or error
            try {
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.tagName("video")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("img[src*='cdninstagram']"))
                ))
            } catch (e: Exception) {
                println("Page loaded but no video/media elements found initially")
            }

            // Give it a bit more time for dynamic content to load
            Thread.sleep(3000)

            println("Page loaded, extracting video URLs...")

            val videoUrls = mutableSetOf<String>()

            // Method 1: Check network logs for video URLs
            val logs = driver.manage().logs().get(LogType.PERFORMANCE)
            for (entry in logs) {
                val message = entry.message
                if (message.contains(".mp4") || message.contains("video")) {
                    // Extract URLs from network log
                    val urlPattern = Regex("""https?://[^\s"']+\.mp4[^\s"']*""")
                    val matches = urlPattern.findAll(message)
                    matches.forEach { match ->
                        val cleanUrl = match.value.replace("\\u0026", "&").replace("\\", "")
                        videoUrls.add(cleanUrl)
                        println("Found video URL in network log: ${cleanUrl.take(100)}...")
                    }
                }
            }

            // Method 2: Check video elements in DOM
            val videoElements = driver.findElements(By.tagName("video"))
            for (videoElement in videoElements) {
                val src = videoElement.getAttribute("src")
                if (!src.isNullOrEmpty() && src.contains("http")) {
                    videoUrls.add(src)
                    println("Found video URL in DOM: ${src.take(100)}...")
                }
            }

            // Method 3: Check for source elements within video tags
            val sourceElements = driver.findElements(By.cssSelector("video source"))
            for (sourceElement in sourceElements) {
                val src = sourceElement.getAttribute("src")
                if (!src.isNullOrEmpty() && src.contains("http")) {
                    videoUrls.add(src)
                    println("Found video URL in source element: ${src.take(100)}...")
                }
            }

            // Method 4: Search page source for video URLs
            val pageSource = driver.pageSource
            val urlPattern = Regex("""https?://[^\s"']+\.mp4[^\s"']*""")
            val matches = urlPattern.findAll(pageSource)
            matches.forEach { match ->
                videoUrls.add(match.value)
                println("Found video URL in page source: ${match.value.take(100)}...")
            }

            println("Total unique video URLs found: ${videoUrls.size}")

            if (videoUrls.isEmpty()) {
                // Save page source for debugging
                File("debug/page_source_selenium.html").writeText(pageSource)
                println("Saved page source to debug/page_source_selenium.html")

                throw IllegalStateException("No videos found in the post. The post may not contain a video, may be private, or may require login.")
            }

            // Convert URLs to VideoVersion objects
            videoUrls.map { url ->
                VideoVersion(url = url, width = null, height = null, type = null)
            }

        } finally {
            driver.quit()
        }
    }

    /**
     * Download a video from URL with progress callback
     */
    suspend fun downloadVideo(
        videoUrl: String,
        destinationFile: File,
        onProgress: (downloaded: Long, total: Long) -> Unit
    ) = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(videoUrl)
            .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .header("Referer", "https://www.threads.net/")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Failed to download video: ${response.code}")
            }

            val body = response.body ?: throw IOException("Empty response body")
            val contentLength = body.contentLength()

            body.byteStream().use { inputStream ->
                destinationFile.outputStream().use { outputStream ->
                    val buffer = ByteArray(8192)
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
}
