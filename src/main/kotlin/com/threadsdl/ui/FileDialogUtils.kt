package com.threadsdl.ui

import com.threadsdl.utils.Constants
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

object FileDialogUtils {
    fun showFileSaveDialog(): File? {
        val fileChooser = JFileChooser().apply {
            dialogTitle = Constants.FILE_CHOOSER_TITLE
            selectedFile = File(Constants.DEFAULT_FILENAME)
            fileFilter = FileNameExtensionFilter(Constants.FILE_TYPE_DESCRIPTION, "mp4")
        }

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            val selectedFile = fileChooser.selectedFile

            // Check if file exists and ask for confirmation
            if (selectedFile.exists()) {
                val result = JOptionPane.showConfirmDialog(
                    null,
                    "The file '${selectedFile.name}' already exists.\nDo you want to replace it?",
                    "File Already Exists",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                )

                return if (result == JOptionPane.YES_OPTION) {
                    selectedFile
                } else {
                    // User chose not to overwrite, show dialog again
                    showFileSaveDialog()
                }
            }

            return selectedFile
        }

        return null
    }
}
