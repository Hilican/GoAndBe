package com.github.hilican.goandbe.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.hilican.goandbe.data.Room.UserRoom
import com.github.hilican.goandbe.domain.DTO.UserRegistrationRequest
import com.github.hilican.goandbe.domain.iRepositories.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor (private val repository: IAuthRepository) : ViewModel() {
    data class UserUiState(
        val userRoom: UserRoom? = null,           // El usuario (nulo al principio)
        val isLoading: Boolean = true,    // Cargando inicial (true por defecto)
        val isSaving: Boolean = false,    // Cargando al darle a guardar
        val errorMessage: String? = null,  // Errores sueltos
        val isAuthenticated: Boolean = false,
        // Para comunicar que se envia, tengo dudas de si dejarlo aqui o hacerlo otra variable.
        val isPasswordResetSent: Boolean = false,
    )
    private val _uiState = MutableStateFlow(UserUiState()) // Toma los valores por defecto

    val uiState: StateFlow<UserUiState> = _uiState

    // Escucha en tiempo real a Firebase (Privado, oculto para la UI)
    private val isLogged: StateFlow<String?> = repository.getLogState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = repository.getCurrentUserId()
        )


    // El trackSessikon lo dejo porque antes lo usaba como metodo principal, para saber si estaba logeado o no
    // y la IA me ha recomendado usarlo simplemente para tener mas en cuenta la conexion con firebase
    // de igual manera no deberia afectar mucho a la app, como mucho complica los tests
    init {
        // Activamos la escucha automática de la sesión
        trackSessionStatus()
    }

    private fun trackSessionStatus() {
        viewModelScope.launch {
            // Cada vez que Firebase diga "el estado de la sesión ha cambiado", este bloque se ejecuta
            isLogged.collect { userId ->
                if (userId != null) {
                    _uiState.update {
                        it.copy(isLoading = true, isAuthenticated = true, errorMessage = null)
                    }
                    // Si hay un ID, cargamos los datos del usuario
                    val user = repository.getUserById(userId)
                    _uiState.update {
                        it.copy(isLoading = false, isAuthenticated = true, userRoom = user)
                    }
                } else {
                    // Si el ID pasa a ser null (no logueado o sesión expirada),
                    // el estado se actualiza y la UI redirigirá a Home automáticamente
                    _uiState.update {
                        it.copy(isLoading = false, isAuthenticated = false, userRoom = null)
                    }
                }
            }
        }
    }


    fun checkSessionAndLoadUser() {
        viewModelScope.launch {
            // Encedemos el cargando y reiniciamos los flags
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null, isAuthenticated = false)
            }

            val currentUserId = isLogged.value

            if (currentUserId != null) {
                try {
                    val user = repository.getUserById(currentUserId)
                    if (user != null) {
                        // ÉXITO: Tenemos el usuario, apagamos el cargando
                        _uiState.update { it.copy(userRoom = user, isLoading = false, isAuthenticated = true) }
                    } else {
                        // ERROR: Sesión activa en Firebase pero no hay datos en la DB local
                        _uiState.update { it.copy(isLoading = false) }
                        showError("Error: Perfil no encontrado")
                    }
                } catch (e: Exception) {
                    // ERROR DE RED / CRASH: Apagamos el loader y mostramos el error
                    _uiState.update { it.copy(isLoading = false) }
                    showError("Error al cargar datos del usuario: ${e.message}")
                }
            } else {
                // NO HAY SESIÓN Apagamos el cargando y marcamos como no autenticado
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun signUp(userInfo: UserRegistrationRequest) {
        viewModelScope.launch {
            when {
                userInfo.username.isBlank() -> {
                    showError("El nombre de usuario no puede estar vacío")
                    return@launch
                }
                userInfo.email.isBlank() -> {
                    showError("El correo electrónico no puede estar vacío")
                    return@launch
                }
                userInfo.password.isBlank() -> {
                    showError("La contraseña no puede estar vacía")
                    return@launch
                }
                userInfo.phoneNumber.isBlank() -> {
                    showError("El número de teléfono no puede estar vacío")
                    return@launch
                }
                userInfo.dateOfBirth <= 0L -> {
                    showError("Debes seleccionar una fecha de nacimiento válida")
                    return@launch
                }
            }

            val addressError = userInfo.address.validate()
            if (addressError != null) {
                showError(addressError.message ?: "Error en la dirección")
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                if (repository.getUserByUsername(userInfo.username) != null) {
                    _uiState.update { it.copy(isLoading = false) }
                    showError("El nombre de usuario ya existe")
                    return@launch
                }

                if (repository.getUserByEmail(userInfo.email) != null) {
                    _uiState.update { it.copy(isLoading = false) }
                    showError("El email ya está registrado")
                    return@launch
                }

                val result = repository.signUp(userInfo)
                result.fold(
                    onSuccess = { user ->
                        _uiState.update { it.copy(userRoom = user, isLoading = false) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(isLoading = false) }
                        showError(error.message ?: "Error desconocido al registrarse")
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                showError("Error inesperado: ${e.message}")
            }
        }
    }

    fun singIn(email: String, password: String)
    {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.signIn(email, password)
            result.fold(
                onSuccess = { userId ->
                    val user = repository.getUserById(userId)

                    if (user != null) {
                        // ÉXITO TOTAL: Guardamos el usuario y apagamos el cargando
                        _uiState.update { it.copy(userRoom = user, isLoading = false, isAuthenticated = true) }
                    } else {
                        // ERROR A MEDIAS: Login en Firebase bien, pero sin perfil
                        // Apagamos el cargando primero y luego disparamos tu función de error
                        _uiState.update { it.copy(isLoading = false) }
                        showError("Error: Sesión iniciada pero perfil no encontrado")
                    }
                },
                onFailure = { error ->
                    // ERROR DE LOGIN: Apagamos el cargando y mostramos el error
                    _uiState.update { it.copy(isLoading = false) }
                    showError(error.message ?: "Error al iniciar sesión")
                }
            )
        }
    }

    fun logOut()
    {
        repository.logOut()
        _uiState.update { it.copy(isAuthenticated = false, userRoom = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun showError(msg : String)
    {
        _uiState.update { it.copy(errorMessage = msg) }
    }
    //IGNORAR POR AHORA
    fun updateUser(updatedUserRoom: UserRoom) {
        viewModelScope.launch {
            // 1. Activamos el modo "Guardando" y limpiamos errores anteriores
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            // 2. Intentamos guardar
            val result = repository.updateUser(updatedUserRoom)

            result.fold(
                onSuccess = { userId ->
                    val user = repository.getUserById(userId) ?: updatedUserRoom
                    // 3. Si va bien, actualizamos el usuario y apagamos isSaving
                    _uiState.update { it.copy(userRoom = user, isSaving = false) }
                },
                onFailure = { error ->
                    // 4. Si falla, apagamos isSaving y ponemos el error (el User queda intacto)
                    _uiState.update { it.copy(isSaving = false, errorMessage = error.message) }
                }
            )
        }
    }
    fun samePasswords(pass1: String, pass2: String) : Boolean
    {
        if(pass1 != pass2)
        {
            showError("Las contrasenyas no coinciden")
            return false
        }
        return true
    }
    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            showError("Por favor, introduce tu correo electrónico")
            return
        }

        viewModelScope.launch {
            // Encendemos el cargando y limpiamos estados de recuperación anteriores
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isPasswordResetSent = false) }

            val result = repository.sendPasswordResetEmail(email)

            result.fold(
                onSuccess = {
                    // Apagamos loader y encendemos el aviso de éxito
                    _uiState.update { it.copy(isLoading = false, isPasswordResetSent = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    showError(error.message ?: "No se pudo enviar el correo de recuperación")
                }
            )
        }
    }

    fun clearPasswordResetSent() {
        _uiState.update { it.copy(isPasswordResetSent = false) }
    }

}