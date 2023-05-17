package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.*
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {
    private val prefs = context.getSharedPreferences("auth",
        Context.MODE_PRIVATE)
    private val _authStateFlow: MutableStateFlow<AuthModel>


    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0)

        _authStateFlow = if (id == 0L || token == null) {
            prefs.edit { clear() }
            MutableStateFlow(AuthModel())
        } else MutableStateFlow(AuthModel(id, token))
    }

    val authStateFlow = _authStateFlow.asStateFlow()

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun apiService(): PostsApiService
    }

//    private fun getApiService(context: Context): PostsApiService{
//        val hiltEntryPoint = EntryPointAccessors.fromApplication(
//            context,
//            AppAuthEntryPoint::class.java
//        )
//        return hiltEntryPoint.apiService()
//    }

    @Synchronized
    fun setUser(user: AuthModel){
        _authStateFlow.value = user
        prefs.edit{
            putLong(ID_KEY,user.id)
            putString(TOKEN_KEY,user.token)
        }
    }

    @Synchronized
    fun removeUser(){
        _authStateFlow.value = AuthModel()
        prefs.edit{clear()}
    }

    companion object {
        private const val ID_KEY = "ID_KEY"
        private const val TOKEN_KEY = "TOKEN_KEY"

        @Volatile
        private var instance: AppAuth? = null

 //      @Synchronized
 //      fun initAppAuth(context: Context): AppAuth{
 //          return instance ?: AppAuth(context).apply { instance = this }
 //      }

 //      fun getInstance():AppAuth = requireNotNull(instance){"initAppAuth was not invoked"}
  }
}


