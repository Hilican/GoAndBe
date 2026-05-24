package com.github.hilican.goandbe.domain.iRepositories

import com.github.hilican.goandbe.data.Room.UserRoom

interface IUserRepository {
    suspend fun getUserByUsername(username: String): UserRoom?
    suspend fun getUserById(userId: String): UserRoom?
    suspend fun getUserByEmail(email: String): UserRoom?
    // CREATE
    suspend fun saveUserLocally(userRoom: UserRoom) : Long
    // UPDATE
    suspend fun updateUserLocally(updatedUserRoom: UserRoom) : Int
    // DELETE
    suspend fun deleteUserLocally(userRoom: UserRoom) : Int
}