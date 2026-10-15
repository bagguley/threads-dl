package com.threadsdl.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.threadsdl.utils.Constants

@Composable
@Preview
fun MainWindow() {
    val viewModel = remember { MainWindowViewModel() }
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.cleanup()
        }
    }

    MaterialTheme(
        colorScheme = ThreadsDarkColorScheme
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Constants.UI_PADDING.dp),
                verticalArrangement = Arrangement.spacedBy(Constants.UI_SPACING.dp)
            ) {
                // Centered Title with Custom Logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ThreadsLogo(size = 40)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = Constants.APP_TITLE,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                UrlInputSection(
                    urlInput = viewModel.urlInput,
                    onUrlChange = { viewModel.onUrlInputChange(it) },
                    enabled = !viewModel.isDownloading
                )

                ActionButtonsRow(
                    onDetectClick = { viewModel.detectVideo(scope) },
                    onDownloadClick = {
                        val file = FileDialogUtils.showFileSaveDialog()
                        if (file != null) {
                            viewModel.downloadVideo(file, scope)
                        }
                    },
                    detectEnabled = !viewModel.isDownloading,
                    downloadEnabled = !viewModel.isDownloading && viewModel.hasDetectedVideo
                )

                HorizontalDivider()

                StatusSection(
                    statusText = viewModel.statusText,
                    progressValue = viewModel.progressValue
                )

                HorizontalDivider()

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    VideoListSection(
                        videoListText = viewModel.videoListText
                    )
                }
            }
        }

        ErrorDialog(
            show = viewModel.showErrorDialog,
            message = viewModel.errorMessage,
            onDismiss = { viewModel.dismissErrorDialog() }
        )

        SuccessDialog(
            show = viewModel.showSuccessDialog,
            message = viewModel.successMessage,
            onDismiss = { viewModel.dismissSuccessDialog() }
        )
    }
}
