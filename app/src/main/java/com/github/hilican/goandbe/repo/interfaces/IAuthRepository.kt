package com.github.hilican.goandbe.repo.interfaces

import com.github.hilican.goandbe.data.Room.UserRoom
import com.github.hilican.goandbe.domain.DTO.UserRegistrationRequest

interface IAuthRepository : IUserRepository, IFirebaseRepository
{
    suspend fun signUp(request: UserRegistrationRequest) : Result<UserRoom>
    suspend fun signIn(email : String, password : String) : Result<String>
    suspend fun updateUser(updatedUserRoom: UserRoom) : Result<String>
}