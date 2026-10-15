package com.threadsdl.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.threadsdl.generated.resources.Res
import com.threadsdl.generated.resources.ic_check
import com.threadsdl.generated.resources.ic_info
import com.threadsdl.utils.Constants

@Composable
fun ErrorDialog(
    show: Boolean,
    message: String,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_info),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    Constants.DIALOG_ERROR_TITLE,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    message,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                FilledTonalButton(onClick = onDismiss) {
                    Text(Constants.BTN_OK)
                }
            },
            containerColor = MaterialTheme.colorScheme.errorContainer,
            iconContentColor = MaterialTheme.colorScheme.error,
            titleContentColor = MaterialTheme.colorScheme.onErrorContainer,
            textContentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
fun SuccessDialog(
    show: Boolean,
    message: String,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_check),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    Constants.DIALOG_SUCCESS_TITLE,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    message,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text(Constants.BTN_OK)
                }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            iconContentColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            textContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
