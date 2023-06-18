package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

sealed interface FeedItem{
     val id:Long
}

data class Post(
    override val id: Long,
    val author: String,
    val authorAvatar: String,
    val authorId: Long,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val hidden: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
):FeedItem

data class Ad(
    override val id:Long,
    val image: String,
):FeedItem

data class Attachment(
    val url: String,
    val type: AttachmentType,
)

data class TimingSeparator(
    override val id:Long,
    val text:String,
) : FeedItem
