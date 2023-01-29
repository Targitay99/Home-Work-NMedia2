package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun likeById(callback: GetAllCallback<Post>,post: Post)
    fun save(callback: GetAllCallback<List<Post>>,post: Post)
    fun removeById(callback: GetAllCallback<List<Post>>,post: Post)

    fun getAllAsync(callback: GetAllCallback<List<Post>>)

    interface GetAllCallback <T> {
        fun onSuccess(data: T) {}
        fun onError(e: Exception) {}
    }
}
