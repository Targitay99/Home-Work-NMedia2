package ru.netology.nmedia.repository

import androidx.paging.PagingData
import ru.netology.nmedia.dto.Media
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun getAll()
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun removeById(post: Post)
    suspend fun likeById(post: Post)
    suspend fun viewNewPost()
    suspend fun upload(upload: MediaUpload): Media
}


