package ru.netology.nmedia.repository


import okhttp3.*
import retrofit2.Call
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit
import retrofit2.Callback
import retrofit2.Response


class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        PostsApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(
                call: Call<List<Post>>, response: Response<List<Post>>
            ) {
                when {
                    !response.isSuccessful -> callback.onError(Exception(response.message()))
                    response.body() == null -> callback.onError(Exception("body is null"))
                    else -> callback.onSuccess(response.body() ?: emptyList())
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })

    }

    override fun removeById(callback: PostRepository.Callback<Unit>, post: Post) {
        val id = post.id
        PostsApi.retrofitService.removeById(id).enqueue(object : Callback<Unit> {
            override fun onResponse(
                call: Call<Unit>,
                response: Response<Unit>
            ) {
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError(Exception(t))
            }

        })
    }

    override fun likeById(callback: PostRepository.Callback<Post>, post: Post) {
        val id = post.id
        if (post.likedByMe) {
            PostsApi.retrofitService.dislikeById(id).enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    when {
                        !response.isSuccessful -> callback.onError(Exception(response.message()))
                        response.body() == null -> callback.onError(Exception("body is null"))
                        else -> callback.onSuccess(response.body() ?: post)
                    }

                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }

            })
        } else PostsApi.retrofitService.likeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                when {
                    !response.isSuccessful -> callback.onError(Exception(response.message()))
                    response.body() == null -> callback.onError(Exception("body is null"))
                    else -> callback.onSuccess(response.body() ?: post)
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }

    override fun save(callback: PostRepository.Callback<Post>, post: Post) {
        PostsApi.retrofitService.save(post).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                callback.onSuccess(response.body()!!)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }
}

