package com.threadsdl.service

import com.threadsdl.models.VideoVersion
import com.threadsdl.utils.Constants
import com.threadsdl.utils.cleanVideoUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.nio.file.Paths
import java.time.Duration

/**
 * Handles video URL extraction from Threads posts using Selenium WebDriver
 */
class VideoExtractor {

    /**
     * Extract post ID from a Threads URL using pattern matching
     */
    fun extractPostId(url: String): String? {
        return Constants.URL_PATTERNS
            .firstNotNullOfOrNull { pattern ->
                pattern.find(url)?.groupValues?.last()
            }
    }

    /**
     * Fetch video URLs using Selenium with priority-based detection
     *
     * @param postUrl The Threads post URL to fetch video from
     * @return List containing the main video version
     */
    suspend fun fetchVideoUrls(postUrl: String): List<VideoVersion> = withContext(Dispatchers.IO) {
        println(Constants.LOG_USING_SELENIUM)

        val driver = ChromeDriver(createChromeOptions())

        try {
            val normalizedUrl = normalizeThreadsUrl(postUrl)
            println("${Constants.LOG_LOADING_URL} $normalizedUrl")

            driver.get(normalizedUrl)
            waitForPageLoad(driver)

            println(Constants.LOG_PAGE_LOADED)

            // Try extraction methods in priority order
            val mainVideoUrl = extractVideoFromDom(driver)
                ?: extractVideoFromNetworkLogs(driver)
                ?: extractVideoFromPageSource(driver)
                ?: run {
                    savePageSourceForDebug(driver)
                    throw IllegalStateException(Constants.ERR_NO_VIDEO_FOUND)
                }

            println(Constants.MSG_SUCCESSFULLY_EXTRACTED)
            listOf(VideoVersion(url = mainVideoUrl, width = null, height = null, type = null, fileSize = null))

        } finally {
            driver.quit()
        }
    }

    /**
     * Create Chrome options configured for headless mode
     */
    private fun createChromeOptions(): ChromeOptions {
        return ChromeOptions().apply {
            Constants.getDefaultChromeArguments().forEach { addArguments(it) }
            addArguments("--user-agent=${Constants.USER_AGENT}")

            // Use modern Selenium 4 approach for logging
            setCapability(
                "goog:loggingPrefs", mapOf(
                    "performance" to "ALL"
                )
            )
        }
    }

    /**
     * Normalize Threads URL to full format
     */
    private fun normalizeThreadsUrl(postUrl: String): String {
        return if (postUrl.startsWith("http")) postUrl
        else "${Constants.THREADS_BASE_URL}/t/$postUrl"
    }

    /**
     * Wait for page to load with timeout
     */
    private suspend fun waitForPageLoad(driver: ChromeDriver) {
        val wait = WebDriverWait(driver, Duration.ofSeconds(Constants.PAGE_LOAD_TIMEOUT_SECONDS))

        try {
            wait.until(
                ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.tagName("video")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("img[src*='${Constants.INSTAGRAM_CDN_MARKER}']"))
                )
            )
        } catch (e: Exception) {
            println("Page loaded but no video/media elements found initially")
        }

        // Use non-blocking delay instead of Thread.sleep
        delay(Constants.ADDITIONAL_WAIT_MILLIS)
    }

    /**
     * Extract video URL from DOM video elements
     */
    private fun extractVideoFromDom(driver: ChromeDriver): String? {
        println(Constants.LOG_CHECKING_DOM)
        val videoElements = driver.findElements(By.tagName("video"))

        if (videoElements.isNotEmpty()) {
            val src = videoElements[0].getAttribute("src")
            if (!src.isNullOrEmpty() && src.contains("http")) {
                val url = src.cleanVideoUrl()
                println("${Constants.LOG_FOUND_IN_DOM} ${url.take(Constants.LOG_URL_PREVIEW_LENGTH)}...")
                return url
            }
        }

        return null
    }

    /**
     * Extract video URL from network performance logs
     */
    private fun extractVideoFromNetworkLogs(driver: ChromeDriver): String? {
        println(Constants.LOG_CHECKING_NETWORK)
        val logs = driver.manage().logs().get(LogType.PERFORMANCE)

        for (entry in logs) {
            val message = entry.message
            if (message.contains(".mp4")) {
                val match = Constants.MP4_URL_PATTERN.find(message)
                if (match != null) {
                    val url = match.value.cleanVideoUrl()
                    println("${Constants.LOG_FOUND_IN_NETWORK} ${url.take(Constants.LOG_URL_PREVIEW_LENGTH)}...")
                    return url
                }
            }
        }

        return null
    }

    /**
     * Extract video URL from page source HTML
     */
    private fun extractVideoFromPageSource(driver: ChromeDriver): String? {
        println(Constants.LOG_CHECKING_PAGE_SOURCE)
        val pageSource = driver.pageSource ?: ""
        val match = Constants.MP4_URL_PATTERN.find(pageSource)

        return match?.let { m ->
            val url = m.value.cleanVideoUrl()
            println("${Constants.LOG_FOUND_IN_SOURCE} ${url.take(Constants.LOG_URL_PREVIEW_LENGTH)}...")
            url
        }
    }

    /**
     * Save page source for debugging purposes
     */
    private fun savePageSourceForDebug(driver: ChromeDriver) {
        val pageSource = driver.pageSource ?: ""
        val debugFile = Paths.get(Constants.DEBUG_DIRECTORY, Constants.DEBUG_FILENAME).toFile()
        debugFile.parentFile?.mkdirs()
        debugFile.writeText(pageSource)
        println(Constants.LOG_SAVED_DEBUG)
    }
}
