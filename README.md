# Threads Video Downloader

A Kotlin desktop application to download videos from Threads posts.

## Features

- Download videos from Threads posts using the post URL
- Support for multiple video qualities
- Progress tracking during download
- User-friendly GUI built with TornadoFX
- Handles Instagram's GraphQL API for fetching video URLs

## Requirements

- Java 21 or higher
- Gradle 9.x or higher
- **Google Chrome** or **Chromium** browser installed (for Selenium WebDriver)

## How to Build

```bash
./gradlew build
```

## How to Run

```bash
./gradlew run
```

Or build a distributable:

```bash
./gradlew installDist
```

The executable will be in `build/install/threads-dl/bin/`

## Dependency Management

Check for dependency updates using the Ben Manes Versions plugin:

```bash
./gradlew dependencyUpdates
```

This will generate a report showing which dependencies have newer versions available. The report can be found at `build/dependencyUpdates/report.txt`

## Usage

1. Launch the application
2. Paste a Threads post URL (e.g., `https://www.threads.net/@username/post/POST_ID`)
3. Click "Fetch Videos" to retrieve available video qualities
4. Choose one of:
   - "Download Best Quality" - automatically downloads the highest quality
   - "Choose Quality & Download" - lets you select from available qualities
5. Select where to save the video
6. Wait for the download to complete

## Supported URL Formats

- `https://www.threads.net/@username/post/POST_ID`
- `https://www.threads.com/@username/post/POST_ID`
- `https://www.threads.net/t/POST_ID`
- `https://www.threads.com/t/POST_ID`

## Technical Details

The application uses **Selenium WebDriver with headless Chrome** to load Threads posts and extract video URLs. This approach:
- Executes JavaScript to render the full page content
- Captures network traffic to find video URLs
- Extracts video elements from the DOM
- Works around Threads' dynamic content loading

Videos are then downloaded in chunks with progress tracking using OkHttp.

### Key Components

- **ThreadsService**: Handles API communication and video downloads
- **MainView**: TornadoFX-based UI
- **ThreadsResponse**: Data models for API responses

### Dependencies

- **Selenium WebDriver** - Headless browser automation
- **OkHttp** - HTTP client for video downloads
- **kotlinx.serialization** - JSON parsing
- **TornadoFX** - JavaFX UI framework
- **kotlinx.coroutines** - Async operations

### How It Works

1. User provides a Threads post URL
2. Selenium launches a headless Chrome browser
3. The browser loads the page and executes JavaScript
4. The application captures network requests and DOM elements
5. Video URLs are extracted from multiple sources:
   - Performance logs (network traffic)
   - Video elements in the DOM
   - Source elements within videos
   - Page source inspection
6. User selects desired quality and downloads the video

## License

MIT
