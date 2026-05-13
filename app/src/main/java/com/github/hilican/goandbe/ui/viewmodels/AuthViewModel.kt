package com.github.hilican.goandbe.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

import com.github.hilican.goandbe.data.User
import com.github.hilican.goandbe.domain.DTO.UserRegistrationRequest
import com.github.hilican.goandbe.domain.IAuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn

class AuthViewModel(private val repository: IAuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val uiState: StateFlow<UiState<User>> = _uiState

    //IsLogged should be true when user has a token, it updates automatically
    val isLogged: StateFlow<Boolean> = repository.getLogState()
        .stateIn(
            scope = viewModelScope, // por lo que entiendo la ata a la app, se muere si se cierra la app
            started = SharingStarted.WhileSubscribed(5000), // Despues de 5s sin que se este mirando la pantalla se "apaga"
            initialValue = repository.isLogged()
        )


    sealed class UiState<out T> {
        object Idle : UiState<Nothing>()
        object Loading : UiState<Nothing>()
        data class Success(val user: User) : UiState<Nothing>()
        data class Error(val message: String) : UiState<Nothing>()
    }

    fun signUp(username: String, email: String, pass: String, dob: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                if (repository.getUserByUsername(username) != null) {
                    _uiState.value = UiState.Error("El nombre de usuario ya existe")
                    return@launch
                }

                if (repository.getUserByEmail(email) != null) {
                    _uiState.value = UiState.Error("El email ya está registrado")
                    return@launch
                }

                val request = UserRegistrationRequest(username, email, pass, dob)
                val result = repository.signUp(request)
                _uiState.value = result.fold(
                    onSuccess = { user -> UiState.Success(user) },
                    onFailure = { error -> UiState.Error(error.message ?: "Error desconocido") }
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error inesperado: ${e.message}")
            }
        }
    }

    fun singIn(email: String, password: String)
    {

    }

    fun logOut()
    {
        repository.logOut()
    }


    suspend fun updateUser(username: String, dob: Long)
    {

    }
}