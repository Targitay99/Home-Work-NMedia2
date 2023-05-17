package ru.netology.nmedia.viewmodel


import androidx.lifecycle.*
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.NewUser
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.model.AuthModel
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth,
    private val apiService: PostsApiService
) : ViewModel() {

    val authLiveData = appAuth.authStateFlow.asLiveData(Dispatchers.Default)
    val isAuthorised: Boolean
        get() = appAuth.authStateFlow.value.token != null


    val _dataState = MutableLiveData(-1)
    val dataState: LiveData<Int>
        get() = _dataState

    init {
        _dataState.value = -1
    }

    fun singIn(user: User) = viewModelScope.launch {
        try {

            val response = apiService.updateUser(user.login, user.password)
                 if (!response.isSuccessful) {
                     _dataState.value = 1
                     //throw ApiError(response.code(), response.message())
                 }
            val body = response.body()
            if (body != null) {
                appAuth.setUser(AuthModel(body.id.toLong(), body.token))
                _dataState.value = 0
            } else {
                appAuth.setUser(AuthModel())
                _dataState.value = 1
            }

        } catch (e: IOException) {
        _dataState.value = 2
        //throw NetworkError
        }
        catch (e: Exception) {
            _dataState.value = 3
        //throw UnknownError
        }
    }

    fun singUp(newUser: NewUser) = viewModelScope.launch {
        try {
            val response = apiService.registerUser(newUser.nameNewUser,newUser.password,newUser.login)
            if (!response.isSuccessful) {
                _dataState.value = 1
                //throw ApiError(response.code(), response.message())
            }
            val body = response.body()
            if (body != null) {
                appAuth.setUser(AuthModel(body.id.toLong(), body.token))
                _dataState.value = 0
            } else {
                //_dataState.value = 0
                appAuth.setUser(AuthModel())
                _dataState.value = 1
            }

        } catch (e: IOException) {
            _dataState.value = 2
            //throw NetworkError
        }
        catch (e: Exception) {
            _dataState.value = 3
            //throw UnknownError
        }
    }
}