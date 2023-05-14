package ru.netology.nmedia.viewmodel


import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.NewUser
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.model.AuthModel
import java.io.IOException


class AuthViewModel(application: Application) : AndroidViewModel(application) {
    val authLiveData = AppAuth.getInstance().authStateFlow.asLiveData(Dispatchers.Default)
    val isAuthorised: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.token != null

    private val _dataState = MutableLiveData(-1)
    val dataState: LiveData<Int>
        get() = _dataState



    fun singIn(user: User) = viewModelScope.launch {
        try {

            val response = PostsApi.service.updateUser(user.login, user.password)
                 if (!response.isSuccessful) {
                     _dataState.value = 1
                     //throw ApiError(response.code(), response.message())
                 }
            val body = response.body()
            if (body != null) {
                AppAuth.getInstance().setUser(AuthModel(body.id.toLong(), body.token))
            } else {
                AppAuth.getInstance().setUser(AuthModel())
                //_dataState.value = 0
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
            val response = PostsApi.service.registerUser(newUser.nameNewUser,newUser.password,newUser.login)
            if (!response.isSuccessful) {
                _dataState.value = 1
                //throw ApiError(response.code(), response.message())
            }
            val body = response.body()
            if (body != null) {
                AppAuth.getInstance().setUser(AuthModel(body.id.toLong(), body.token))
            } else {
                //_dataState.value = 0
                AppAuth.getInstance().setUser(AuthModel())
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