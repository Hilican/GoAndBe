package com.github.hilican.goandbe.domain

import com.github.hilican.goandbe.data.User

interface IUserRepository {
    suspend fun getUserByUsername(username: String): User?
    suspend fun getUserById(userId: String): User?
    suspend fun getUserByEmail(email: String): User?
    // CREATE
    suspend fun saveUserLocally(user: User)
    // UPDATE
    suspend fun updateUserLocally(updatedUser: User)

    // DELETE
    suspend fun deleteUserLocally(user: User)
}