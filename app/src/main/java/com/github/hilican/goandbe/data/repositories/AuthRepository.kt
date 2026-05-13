package com.github.hilican.goandbe.data.repositories

import com.github.hilican.goandbe.data.User
import com.github.hilican.goandbe.data.UserDao
import com.github.hilican.goandbe.domain.DTO.UserRegistrationRequest
import com.github.hilican.goandbe.domain.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository (private val userDao: UserDao, private val firebaseAuth: FirebaseAuth) : IAuthRepository {
    // -- IUserRepository --
    // READ
    override suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    override suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    // CREATE
    override suspend fun saveUserLocally(user: User) {
        userDao.insertUser(user)
    }

    // UPDATE
    override suspend fun updateUserLocally(updatedUser: User) {
        userDao.updateUser(updatedUser)
    }

    // DELETE
    override suspend fun deleteUserLocally(user: User) {
        userDao.deleteUser(user)
    }

    // -- IFirebaseRepository --
    override fun getLogState(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null) // Emite el nuevo estado
        }
        firebaseAuth.addAuthStateListener(listener)

        // Si el Flow se cancela, quitamos el listener para no gastar memoria
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override fun isLogged(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun registerAccount(
        email: String,
        pass: String
    ): Result<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logOut(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getLoggedUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    // -- FUNCIONES AÑADIDAS --
    override suspend fun signUp(request: UserRegistrationRequest) : Result<User>
    {
        return try {
            // Intentamos crear el usuario en Firebase
            val firebaseResult = registerAccount(request.email, request.password)

            // si Firebase tuvo éxito se guarda en local
            if (firebaseResult.isSuccess) {
                val uid = getLoggedUserId() ?: return Result.failure(Exception("No UID")) // Return por si falla, para que no siga
                val newUser = request.toEntity(uid)
                saveUserLocally(newUser)
                Result.success(newUser)
            } else {
                // Si Firebase falló (ej. email ya usado), devolvemos ese error
                Result.failure(firebaseResult.exceptionOrNull() ?: Exception("Error en Firebase"))
            }
        } catch (e: Exception) {
            // Capturamos cualquier error inesperado
            Result.failure(e)
        }
    }

    private fun UserRegistrationRequest.toEntity(firebaseId: String): User {
        return User(
            userId = firebaseId,
            email = this.email,
            username = this.username,
            dateOfBirth = this.dateOfBirth,
            createdAt = System.currentTimeMillis()
        )
    }
}