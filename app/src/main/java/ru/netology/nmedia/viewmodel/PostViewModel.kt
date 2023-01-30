package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.GetAllCallback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun removeById(post: Post) {
        // Очень оптимистический вариант)
        val old = _data.value?.posts.orEmpty()
        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                .filter { it.id != post.id }
            )
        )
        _data.value = FeedModel(loading = true)
        try {
            repository.removeById(object : PostRepository.GetAllCallback<List<Post>> {
                override fun onSuccess(posts: List<Post>) {}

                override fun onError(e: Exception) {}

            }, post)
        } catch (e: IOException) {
            _data.postValue(_data.value?.copy(posts = old))
        }
    }


        fun save() {
            edited.value?.let {
                repository.save(object : PostRepository.GetAllCallback<Post> {
                    override fun onSuccess(posts: Post) {}

                    override fun onError(e: Exception) {}

                }, post = it)
                _postCreated.postValue(Unit)
            }
            edited.value = empty
        }


        fun edit(post: Post) {
            edited.value = post
        }

        fun changeContent(content: String) {
            val text = content.trim()
            if (edited.value?.content == text) {
                return
            }
            edited.value = edited.value?.copy(content = text)
        }

        fun likeById(post: Post) {
            repository.likeById(object : PostRepository.GetAllCallback<Post> {
                override fun onSuccess(post: Post) {
                    _data.postValue(
                        _data.value?.copy(posts = _data.value?.posts.orEmpty()
                            .map { if (it.id == post.id) post else it }
                        )
                    )
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            }, post)

        }

    }
