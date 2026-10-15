package com.threadsdl.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContainingThread(
    @SerialName("thread_items")
    val threadItems: List<ThreadItem>? = null
)

@Serializable
data class ThreadItem(
    val post: Post? = null
)

@Serializable
data class Post(
    @SerialName("video_versions")
    val videoVersions: List<VideoVersion>? = null,
    @SerialName("carousel_media")
    val carouselMedia: List<CarouselMedia>? = null,
    val caption: Caption? = null,
    val code: String? = null,
    @SerialName("has_audio")
    val hasAudio: Boolean? = null
)

@Serializable
data class VideoVersion(
    val url: String,
    val width: Int? = null,
    val height: Int? = null,
    val type: Int? = null
)

@Serializable
data class CarouselMedia(
    @SerialName("video_versions")
    val videoVersions: List<VideoVersion>? = null,
    @SerialName("image_versions2")
    val imageVersions2: ImageVersions? = null
)

@Serializable
data class ImageVersions(
    val candidates: List<ImageCandidate>? = null
)

@Serializable
data class ImageCandidate(
    val url: String,
    val width: Int? = null,
    val height: Int? = null
)

@Serializable
data class Caption(
    val text: String? = null
)
