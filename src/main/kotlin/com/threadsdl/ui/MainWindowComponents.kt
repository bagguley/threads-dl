package com.threadsdl.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.threadsdl.generated.resources.Res
import com.threadsdl.generated.resources.ic_play_arrow
import com.threadsdl.generated.resources.ic_search
import com.threadsdl.utils.Constants

@Composable
fun UrlInputSection(
    urlInput: String,
    onUrlChange: (String) -> Unit,
    enabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = urlInput,
                onValueChange = onUrlChange,
                label = { Text(Constants.URL_FIELD_LABEL) },
                placeholder = { Text(Constants.URL_PLACEHOLDER) },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                singleLine = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_play_arrow),
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
fun ActionButtonsRow(
    onDetectClick: () -> Unit,
    onDownloadClick: () -> Unit,
    detectEnabled: Boolean,
    downloadEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Constants.BUTTON_SPACING.dp)
    ) {
        FilledTonalButton(
            onClick = onDetectClick,
            enabled = detectEnabled,
            modifier = Modifier.weight(1f).height(48.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_search),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                Constants.BTN_DETECT_VIDEO,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Button(
            onClick = onDownloadClick,
            enabled = downloadEnabled,
            modifier = Modifier.weight(1f).height(48.dp)
        ) {
            Text(
                "↓ ${Constants.BTN_DOWNLOAD_VIDEO}",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun StatusSection(
    statusText: String,
    progressValue: Float
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progressValue,
        animationSpec = tween(durationMillis = 300)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = Constants.STATUS_LABEL,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                strokeCap = StrokeCap.Round,
            )
        }
    }
}

@Composable
fun VideoListSection(
    videoListText: String
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = Constants.VIDEO_LIST_LABEL,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 1.dp
            ) {
                Text(
                    text = videoListText,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
