package com.github.hilican.goandbe.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.hilican.goandbe.data.UserDao
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

import com.github.hilican.goandbe.data.User

class AuthViewModel(private val userDao: UserDao) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    val isUserLoggedIn: Boolean
        get() = auth.currentUser != null

    var isLoading by mutableStateOf(false)
        private set // Solo el ViewModel puede modificarlo

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Variable de estado que la UI observará
    var currentUserData by mutableStateOf<User?>(null)
        private set

    // Lógica de Login
    fun login(email: String, password: String, onSuccess: () -> Unit) {
        // Validación básica (opcional, pero recomendada)
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Por favor, rellena todos los campos"
            return
        }

        executeAuthAction(onSuccess = onSuccess) {
            auth.signInWithEmailAndPassword(email, password)
        }
    }

    fun logout() {
        auth.signOut()
    }

    // Lógica de SignUp
    fun signIn(email: String, password: String, username: String, onSuccess: () -> Unit) {
        if (password.length < 6) {
            errorMessage = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        viewModelScope.launch {
            // Buscamos si los datos UNICOS ya existe en Room antes de ir a Firebase
            val existingUser = userDao.getUserByUsername(username)

            if (existingUser != null) {
                errorMessage = "Este nombre de usuario ya está en uso"
                return@launch // Frenamos aquí, no se ejecuta nada más
            }

            // Si los datos UNICOS están libres, procedemos con Firebase
            executeAuthAction(
                onSuccess = {
                    viewModelScope.launch {
                        try {
                            val uid = auth.currentUser?.uid ?: return@launch
                            val nuevoUsuario = User(userId = uid, email = email, username = username)

                            userDao.insertUser(nuevoUsuario)
                            isLoading = false
                            onSuccess()
                        } catch (e: Exception) {
                            // Este catch ahora es casi imposible que salte, pero es bueno tenerlo
                            isLoading = false
                            errorMessage = "Error inesperado al guardar el perfil"
                        }
                    }
                }
            ) {
                auth.createUserWithEmailAndPassword(email, password)
            }
        }
    }

    // De momento lo ignoro
    fun sendPasswordReset(email: String, onSuccess: () -> Unit) {
        if (email.isBlank()) {
            errorMessage = "Introduce tu email para enviarte el enlace"
            return
        }
        executeAuthAction(onSuccess){auth.sendPasswordResetEmail(email)}
    }
    private fun executeAuthAction(
        onSuccess: (() -> Unit)? = null,
        action: () -> com.google.android.gms.tasks.Task<*>,
    ) {
        isLoading = true
        errorMessage = null

        // Ejecutamr la acción
        action().addOnCompleteListener { task ->
            isLoading = false
            if (task.isSuccessful) {
                if (onSuccess != null) {
                    onSuccess()
                } else {
                    isLoading = false
                }
            }else {
                isLoading = false
                errorMessage = task.exception?.localizedMessage ?: "Error inesperado"
            }
        }
    }

    fun updateProfile(newUsername: String, newDob: Long, onComplete: () -> Unit) {
        val uid = auth.currentUser?.uid
        val email = auth.currentUser?.email

        // Validamos que el usuario esté logueado (importante para que no salga en silencio)
        if (uid == null || email == null) {
            errorMessage = "Error: No hay sesión iniciada."
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val updatedUser = User(
                    userId = uid,
                    email = email,
                    username = newUsername,
                    dateOfBirth = newDob
                )
                userDao.updateUser(updatedUser) // Al tener el mismo ID, Room lo sobrescribirá (REPLACE)
                errorMessage = null
                onComplete()
            } catch (e: Exception) {
                errorMessage = "Error al actualizar el perfil"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchUserData() {
        // Obtenemos el ID del usuario que tiene la sesión iniciada en Firebase
        val uid = auth.currentUser?.uid

        if (uid != null) {
            viewModelScope.launch {
                isLoading = true
                try {
                    // Llamamos a Room
                    val user = userDao.getUserById(uid)

                    // Actualizamos el estado
                    currentUserData = user
                } catch (e: Exception) {
                    errorMessage = "Error al cargar datos locales: ${e.localizedMessage}"
                } finally {
                    isLoading = false
                }
            }
        } else {
            // Si no hay sesión, limpiamos los datos
            currentUserData = null
        }
    }

    fun clearError() {
        errorMessage = null
    }
}