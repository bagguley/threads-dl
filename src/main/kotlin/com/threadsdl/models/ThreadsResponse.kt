package com.threadsdl.models

import kotlinx.serialization.Serializable

/**
 * Represents a video version with optional metadata
 */
@Serializable
data class VideoVersion(
    val url: String,
    val width: Int? = null,
    val height: Int? = null,
    val type: Int? = null,
    val fileSize: Long? = null
)
