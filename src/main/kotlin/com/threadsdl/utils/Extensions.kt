package com.threadsdl.utils

import java.text.DecimalFormat

/**
 * Extension functions for common operations
 */

/**
 * Cleans video URL by replacing HTML entities and escape sequences
 */
fun String.cleanVideoUrl(): String {
    return this
        .replace("\\u0026", "&")
        .replace("&amp;", "&")
        .replace("\\", "")
}

/**
 * Converts bytes to megabytes
 */
fun Long.toMegabytes(): Double = this / Constants.BYTES_PER_MB

/**
 * Formats a double as a size string with 2 decimal places
 */
fun Double.formatSize(): String = DecimalFormat("#.##").format(this)

/**
 * Formats byte count as MB string
 */
fun Long.formatAsMegabytes(): String = this.toMegabytes().formatSize()
