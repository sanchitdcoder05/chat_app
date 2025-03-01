package com.example.chatapp.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("UPDATE user_table SET username = :username WHERE id = 1")
    suspend fun updateUser(username: String)

    @Query("SELECT username FROM user_table WHERE id = 1")
    suspend fun getUsername(): String?
}