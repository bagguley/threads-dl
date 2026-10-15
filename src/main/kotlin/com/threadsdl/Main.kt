package com.threadsdl

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.threadsdl.ui.MainWindow
import com.threadsdl.utils.Constants
import java.awt.Dimension

fun main() = application {
    val windowState = rememberWindowState(
        width = Constants.MIN_WINDOW_WIDTH.dp,
        height = Constants.MIN_WINDOW_HEIGHT.dp,
        position = WindowPosition.Aligned(androidx.compose.ui.Alignment.Center)
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = Constants.APP_TITLE,
        state = windowState,
        resizable = true
    ) {
        // Set minimum and maximum dimensions on the underlying AWT window
        LaunchedEffect(Unit) {
            window.minimumSize = Dimension(
                Constants.MIN_WINDOW_WIDTH.toInt(),
                Constants.MIN_WINDOW_HEIGHT.toInt()
            )
            window.maximumSize = Dimension(
                Constants.MIN_WINDOW_WIDTH.toInt(),
                Int.MAX_VALUE // Allow unlimited vertical resizing
            )
        }

        MainWindow()
    }
}
