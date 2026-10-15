package com.threadsdl.utils

/**
 * Application-wide constants for configuration and string literals
 */
object Constants {
    // UI Layout Constants
    const val MIN_WINDOW_WIDTH = 650.0
    const val MIN_WINDOW_HEIGHT = 650.0
    const val UI_SPACING = 15.0
    const val UI_PADDING = 20.0
    const val BUTTON_SPACING = 10.0
    const val VIDEO_LIST_HEIGHT = 100.0
    const val URL_PREVIEW_LENGTH = 80
    const val LOG_URL_PREVIEW_LENGTH = 100

    // UI Text
    const val APP_TITLE = "Threads Video Downloader"
    const val URL_PLACEHOLDER = "https://www.threads.net/@username/post/..."
    const val URL_FIELD_LABEL = "URL:"
    const val STATUS_LABEL = "Status:"
    const val VIDEO_LIST_LABEL = "Available Videos:"

    // Button Labels
    const val BTN_DETECT_VIDEO = "Detect Video"
    const val BTN_DOWNLOAD_VIDEO = "Download Video"
    const val BTN_OK = "OK"

    // Network Constants
    const val THREADS_BASE_URL = "https://www.threads.net"
    const val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    // File Constants
    const val DEFAULT_FILENAME = "threads_video.mp4"
    const val FILE_TYPE_DESCRIPTION = "MP4 Video"
    const val FILE_CHOOSER_TITLE = "Save Video"
    const val DEBUG_DIRECTORY = "debug"
    const val DEBUG_FILENAME = "page_source_selenium.html"

    // Download Constants
    const val BUFFER_SIZE = 8192
    const val BYTES_PER_MB = 1024.0 * 1024.0

    // Selenium Constants
    const val PAGE_LOAD_TIMEOUT_SECONDS = 15L
    const val ADDITIONAL_WAIT_MILLIS = 3000L
    const val INSTAGRAM_CDN_MARKER = "cdninstagram"

    // Chrome Arguments
    const val CHROME_HEADLESS = "--headless=new"
    const val CHROME_NO_SANDBOX = "--no-sandbox"
    const val CHROME_DISABLE_DEV_SHM = "--disable-dev-shm-usage"
    const val CHROME_DISABLE_GPU = "--disable-gpu"
    const val CHROME_WINDOW_SIZE = "--window-size=1920,1080"

    // Status Messages
    const val MSG_DETECTING = "Detecting video..."
    const val MSG_DOWNLOADING = "Downloading video..."
    const val MSG_NO_VIDEO_FOUND = "No video found"
    const val MSG_DOWNLOAD_COMPLETE = "Download completed successfully!"
    const val MSG_VIDEO_READY = "✓ Main post video ready to download"
    const val MSG_FOUND_MAIN_VIDEO = "✓ Found main post video. Ready to download."
    const val MSG_SUCCESSFULLY_EXTRACTED = "✓ Successfully extracted main post video"

    // Error Messages
    const val ERR_PLEASE_DETECT_FIRST = "Please detect video first"
    const val ERR_ENTER_URL = "Please enter a Threads URL"
    const val ERR_INVALID_URL = "Invalid Threads URL"
    const val ERR_INVALID_URL_FORMAT = "URL must be a valid Threads URL (e.g., https://www.threads.net/@username/post/...)"
    const val ERR_NO_VIDEO_FOUND = "No video found in the post. The post may not contain a video, may be private, or may require login."
    const val ERR_FAILED_TO_FETCH = "Failed to fetch videos:"
    const val ERR_FAILED_TO_DOWNLOAD = "Failed to download video:"
    const val ERR_DOWNLOAD_FAILED = "Failed to download video:"

    // Dialog Titles
    const val DIALOG_ERROR_TITLE = "Error"
    const val DIALOG_SUCCESS_TITLE = "Success"

    // Log Messages
    const val LOG_USING_SELENIUM = "=== Using Selenium to load Threads page ==="
    const val LOG_LOADING_URL = "Loading URL:"
    const val LOG_PAGE_LOADED = "Page loaded, extracting main video URL..."
    const val LOG_CHECKING_DOM = "Checking DOM video elements..."
    const val LOG_CHECKING_NETWORK = "No video in DOM, checking network logs..."
    const val LOG_CHECKING_PAGE_SOURCE = "No video in logs, checking page source..."
    const val LOG_FOUND_IN_DOM = "✓ Found main video in DOM:"
    const val LOG_FOUND_IN_NETWORK = "✓ Found main video in network log:"
    const val LOG_FOUND_IN_SOURCE = "✓ Found main video in page source:"
    const val LOG_SAVED_DEBUG = "Saved page source to debug/page_source_selenium.html"

    // Regex Patterns
    val URL_PATTERNS = listOf(
        Regex("""threads\.(net|com)/@[^/]+/post/([A-Za-z0-9_-]+)"""),
        Regex("""threads\.(net|com)/t/([A-Za-z0-9_-]+)"""),
        Regex("""threads\.(net|com).*post/([A-Za-z0-9_-]+)""")
    )
    val MP4_URL_PATTERN = Regex("""https?://[^\s"']+\.mp4[^\s"']*""")

    /**
     * Get default Chrome arguments for headless mode
     */
    fun getDefaultChromeArguments() = listOf(
        CHROME_HEADLESS,
        CHROME_NO_SANDBOX,
        CHROME_DISABLE_DEV_SHM,
        CHROME_DISABLE_GPU,
        CHROME_WINDOW_SIZE
    )
}
