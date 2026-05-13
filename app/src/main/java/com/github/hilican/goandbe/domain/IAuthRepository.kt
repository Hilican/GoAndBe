package com.github.hilican.goandbe.domain

import com.github.hilican.goandbe.data.User
import com.github.hilican.goandbe.domain.DTO.UserRegistrationRequest

interface IAuthRepository : IUserRepository, IFirebaseRepository
{
    suspend fun signUp(request: UserRegistrationRequest) : Result<User>

}