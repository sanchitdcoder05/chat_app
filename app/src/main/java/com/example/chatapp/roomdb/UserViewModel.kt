package com.example.chatapp.roomdb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider

class UserViewModel(private val userDao: UserDao) : ViewModel() {

    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username

    init {
        fetchUsername()
    }

    fun saveUsername(username: String) {
        viewModelScope.launch {
            val existingUsername = userDao.getUsername()
            if (existingUsername == null) {
                userDao.insertUser(User(id = 1, username = username))
            } else {
                userDao.updateUser(username)
            }
            _username.value = username
        }
    }

    private fun fetchUsername() {
        viewModelScope.launch {
            _username.value = userDao.getUsername()
        }
    }
}


class UserViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}