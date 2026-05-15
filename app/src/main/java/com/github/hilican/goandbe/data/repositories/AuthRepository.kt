package com.github.hilican.goandbe.data.repositories

import com.github.hilican.goandbe.data.User
import com.github.hilican.goandbe.data.UserDao
import com.github.hilican.goandbe.domain.DTO.UserRegistrationRequest
import com.github.hilican.goandbe.domain.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor (private val userDao: UserDao, private val firebaseAuth: FirebaseAuth) : IAuthRepository {
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
    override suspend fun saveUserLocally(user: User) : Long {
        return userDao.insertUser(user)
    }

    // UPDATE
    override suspend fun updateUserLocally(updatedUser: User): Int {
        return userDao.updateUser(updatedUser)
    }

    // DELETE
    override suspend fun deleteUserLocally(user: User): Int {
        return userDao.deleteUser(user)
    }

    // -- IFirebaseRepository --
    override fun getLogState(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            // Si currentUser existe, mandamos su uid. Si es null, mandamos null.
            val userId = auth.currentUser?.uid
            trySend(userId)
        }

        firebaseAuth.addAuthStateListener(listener)

        // La limpieza sigue siendo igual de importante
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

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

    override suspend fun deleteUserFirebase(): Result<Unit> {
        return try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                currentUser.delete().await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se pudo borrar: No hay ningún usuario con sesión activa."))
            }
        } catch (e: Exception) {
            // Si Firebase da error (ej. pide autenticación reciente o no hay internet)
            Result.failure(e)
        }
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
                try {
                    val insertedRowId = saveUserLocally(newUser)

                    // Verificamos si Room devolvió -1 (error de inserción)
                    if (insertedRowId != -1L) {
                        Result.success(newUser)
                    } else {
                        // Room no explotó, pero no guardó nada. Hacemos marcha atrás.
                        val rollback = deleteUserFirebase()
                        val mensajeError = if (rollback.isSuccess) {
                            "Error en la base de datos local. El registro fue cancelado."
                        } else {
                            "Error en la base de datos local y FALLÓ la limpieza en Firebase: ${rollback.exceptionOrNull()?.localizedMessage}"
                        }
                        Result.failure(Exception(mensajeError))
                    }
                } catch (roomException: Exception) {
                    // Room explotó (ej. espacio lleno). Hacemos rollback.
                    val rollback = deleteUserFirebase()
                    val mensajeError = if (rollback.isSuccess) {
                        "Error local al guardar perfil: ${roomException.localizedMessage}. El registro en la nube se limpió correctamente."
                    } else {
                        "Error local: ${roomException.localizedMessage}. ADVERTENCIA CRÍTICA: No se pudo eliminar la cuenta de Firebase: ${rollback.exceptionOrNull()?.localizedMessage}"
                    }
                    Result.failure(Exception(mensajeError))
                }
            } else {
                // Si Firebase falló (ej. email ya usado), devolvemos ese error
                Result.failure(firebaseResult.exceptionOrNull() ?: Exception("Error en Firebase"))
            }
        } catch (e: Exception) {
            // Capturamos cualquier error inesperado
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<String>
    {
        return try {
            val firebaseResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()

            val firebaseUser = firebaseResult.user

            if (firebaseUser != null) {
                Result.success(firebaseUser.uid)
            } else {
                Result.failure(Exception("Error: El usuario es nulo tras el login"))
            }
        } catch (e: Exception) {
            // Captura errores comunes (contraseña incorrecta, usuario no existe, etc.)
            Result.failure(e)
        }
    }

    override suspend fun updateUser(
        updatedUser: User
    ): Result<String> {
        return try {
            val rowsAffected = updateUserLocally(updatedUser)
            if (rowsAffected > 0) {
                Result.success("Usuario actualizado correctamente")
            } else {
                Result.failure(Exception("No se pudo actualizar: El usuario no existe en la base de datos local."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    {
        return try {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
            // Si llega aquí, es que ha ido bien
            Result.success(Unit)
        } catch (e: Exception) {
            // Si Firebase devuelve un error (ej. email no existe)
            Result.failure(e)
        }
    }

    private fun UserRegistrationRequest.toEntity(firebaseId: String): User {
        return User(
            userId = firebaseId,
            email = this.email,
            username = this.username,
            dateOfBirth = this.dateOfBirth,
            createdAt = System.currentTimeMillis(),
            address = this.address,
            phoneNumber = this.phoneNumber,
            receiveEmails = this.receiveEmails
        )
    }
}