package ru.netology.nmedia.viewmodel


import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import java.time.OffsetDateTime
import javax.inject.Inject
import kotlin.random.Random

private val empty = Post(
    id = 0,
    content = "",
    authorId = 0,
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = "",
)

private val noPhoto = PhotoModel()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel

class PostViewModel @Inject constructor(

    private val repository: PostRepository,
    appAuth: AppAuth
) : ViewModel() {


    @RequiresApi(Build.VERSION_CODES.O)
    private val cached: Flow<PagingData<FeedItem>> = repository
        .data
        .map { pagingData ->
            pagingData.insertSeparators(
                generator = { prev, next ->
                    val currentTime = OffsetDateTime.now().toEpochSecond()
                    if ((prev is Post && next is Post)) {
                        val howOlderPrev = agoToText(
                            (currentTime - prev.published.toLong()).toInt(),
                            inLastWeekText = "На прошлой неделе", //Здесь нужна помощь, не знаю как обратиться к строковому ресурсу.
                            inTodayText = "сегодня",
                            inYesterdayText = "вчера",
                        )
                        val howOlderNext = agoToText(
                            (currentTime - next.published.toLong()).toInt(),
                            inLastWeekText = "На прошлой неделе",
                            inTodayText = "сегодня",
                            inYesterdayText = "вчера",
                        )
                        when {
                            (howOlderPrev != howOlderNext) -> {
                                TimingSeparator(
                                    Random.nextLong(),
                                    howOlderNext
                                )
                            }
                            (prev.id.rem(5) == 0L) -> {
                                Ad(
                                    Random.nextLong(),
                                    "figma.jpg"
                                )
                            }
                            else -> null
                        }

                    } else {
                        null
                    }
                }
            )
        }
        .cachedIn(viewModelScope)

    @RequiresApi(Build.VERSION_CODES.O)
    val data: Flow<PagingData<FeedItem>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    if (post is Post) {
                        post.copy(ownedByMe = post.authorId == myId)
                    } else {
                        post
                    }
                }
            }
        }.flowOn(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState


    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            // repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }


    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    when (_photo.value) {
                        noPhoto -> repository.save(it)
                        else -> _photo.value?.file?.let { file ->
                            repository.saveWithAttachment(it, MediaUpload(file))
                        }
                    }
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
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

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun removeById(post: Post) = viewModelScope.launch {
        try {
            repository.removeById(post)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun likeById(post: Post) = viewModelScope.launch {
        try {
            repository.likeById(post)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    private fun agoToText(
        timeInSecond: Int,
        inLastWeekText: String,
        inYesterdayText: String,
        inTodayText: String,

        ) = when (timeInSecond) {
        in 0..86400 -> inTodayText
        in 86401..172800 -> inYesterdayText
        in 172801..2592000 -> inLastWeekText
        else -> inLastWeekText
    }
}

