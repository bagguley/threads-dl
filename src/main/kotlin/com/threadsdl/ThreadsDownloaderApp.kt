package com.threadsdl

import com.threadsdl.ui.MainView
import javafx.stage.Stage
import tornadofx.*

class ThreadsDownloaderApp : App(MainView::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.isResizable = true
        stage.minWidth = 650.0
        stage.minHeight = 450.0
    }
}
