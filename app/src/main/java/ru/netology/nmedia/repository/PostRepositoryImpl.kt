package ru.netology.nmedia.repository

import androidx.lifecycle.*
import okio.IOException
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {

    override val data = dao.getAll().map(List<PostEntity>::toDto)

    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(post: Post) {
        dao.removeById(post.id)
        try {
            PostsApi.service.removeById(post.id)
        } catch (e: IOException) {
            dao.insert(PostEntity.fromDto(post))
            throw NetworkError

        } catch (e: Exception) {
            dao.insert(PostEntity.fromDto(post))
            throw UnknownError
        }
    }

    override suspend fun likeById(post: Post) {
        val id = post.id
        dao.likeById(id)
        try {
            if (post.likedByMe){
                PostsApi.service.dislikeById(id)
            }else PostsApi.service.likeById(id)
        }catch (e: IOException) {
            dao.likeById(id)
            throw NetworkError

        } catch (e: Exception) {
            dao.likeById(id)
            throw UnknownError
        }
    }
}
