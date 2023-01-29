package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}


    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAllAsync(callback: PostRepository.GetAllCallback<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun removeById(callback: PostRepository.GetAllCallback<List<Post>>, post: Post) {
        val id = post.id
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {

                    // TODO Здесь не могу получить список постов, я так понимаю что из-за запроса
                    //  .delete()
                    //  .url("${BASE_URL}/api/slow/posts/$id")

                //    val body = response.body?.string() ?: throw RuntimeException("body is null")
                //    try {
                //        callback.onSuccess(gson.fromJson(body, typeToken.type))
                //    } catch (e: Exception) {
                //        callback.onError(e)
                //    }
                }
            })
    }

    override fun likeById(callback: PostRepository.GetAllCallback<Post>, post: Post) {
        val id = post.id
        val request = if (post.likedByMe) {
            Request.Builder()
                .delete(gson.toJson(post.id).toRequestBody(jsonType))
                .url("${BASE_URL}/api/posts/$id/likes")
                .build()
        } else {
            Request.Builder()
                .post(gson.toJson(post.id).toRequestBody(jsonType))
                .url("${BASE_URL}/api/posts/$id/likes")
                .build()
        }
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    callback.onSuccess(gson.fromJson(body, Post::class.java))

                }

            })
    }

    override fun save(callback: PostRepository.GetAllCallback<List<Post>>, post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    // TODO Здесь не могу получить список постов, я так понимаю что из-за запроса
                    //val body = response.body?.string() ?: throw RuntimeException("body is null")
                    //    try {
                    //        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    //    } catch (e: Exception) {
                    //        callback.onError(e)
                    //    }
                }
            })
    }
}
