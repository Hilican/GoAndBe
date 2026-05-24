package com.github.hilican.goandbe.domain.iRepositories

import kotlinx.coroutines.flow.Flow

interface IFirebaseRepository {
    fun getLogState() : Flow<String?>
    fun getLoggedUserId(): String?
    suspend fun registerAccount(email: String, pass: String) : Result<Unit>
    fun getCurrentUserId(): String?
    fun logOut() : Result<Unit>
    suspend fun deleteUserFirebase() : Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}