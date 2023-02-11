package ru.netology.nmedia.repository

import retrofit2.Callback
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun likeById(callback: Callback<Post>,post: Post)
    fun save(callback: Callback<Post>,post: Post)
    fun removeById(callback: Callback<Unit>, post: Post)

    fun getAllAsync(callback: Callback<List<Post>>)

    interface Callback <T> {
        fun onSuccess(data: T) {}
        fun onError(e: Exception) {}
    }
}
