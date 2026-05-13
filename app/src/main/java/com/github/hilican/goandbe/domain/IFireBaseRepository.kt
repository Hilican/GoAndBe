package com.github.hilican.goandbe.domain

import kotlinx.coroutines.flow.Flow

interface IFirebaseRepository {
    fun getLogState() : Flow<Boolean>
    fun isLogged() : Boolean

    fun getLoggedUserId(): String?

    suspend fun registerAccount(email: String, pass: String) : Result<Unit>

    fun logOut() : Result<Unit>
}