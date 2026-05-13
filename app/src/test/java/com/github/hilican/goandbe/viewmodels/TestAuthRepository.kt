package com.github.hilican.goandbe.viewmodels

import com.github.hilican.goandbe.data.User
import com.github.hilican.goandbe.domain.IUserRepository

class TestAuthRepository : IUserRepository {
    private val usersList = mutableListOf<User>()
    override suspend fun getUserByUsername(username: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(userId: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun insertUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(updatedUser: User) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(user: User) {
        TODO("Not yet implemented")
    }
}