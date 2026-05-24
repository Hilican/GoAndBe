package com.github.hilican.goandbe.viewmodels

import com.github.hilican.goandbe.MainDispatcherRule
import com.github.hilican.goandbe.domain.DTO.UserRegistrationRequest
import com.github.hilican.goandbe.domain.iRepositories.IAuthRepository
import com.github.hilican.goandbe.domain.UserMock
import com.github.hilican.goandbe.ui.viewmodels.AuthViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AuthViewModel
    private val repository: IAuthRepository = mockk(relaxed = true)

    @Before
    fun setup() {
        // Configuramos el mock para que getLogState devuelva un flow vacío por defecto
        every { repository.getLogState() } returns flowOf(null)
        every { repository.getCurrentUserId() } returns null

        viewModel = AuthViewModel(repository)
    }

    @Test
    fun `signUp - cuando el nombre de usuario ya existe, devuelve Error`() = runTest {
        // GIVEN
        val username = "testUser"
        // 1. Necesitamos una dirección válida para que supere la validación local .validate()
        val userInfoRequest = UserRegistrationRequest(
            username = UserMock.mockUserRoom.username,
            email = UserMock.mockUserRoom.email,
            password = "password123",
            dateOfBirth = UserMock.mockUserRoom.dateOfBirth,
            address = UserMock.mockAddress,
            phoneNumber = UserMock.mockUserRoom.phoneNumber,
            receiveEmails = UserMock.mockUserRoom.receiveEmails
        )

        coEvery { repository.getUserByUsername(UserMock.mockUserRoom.username) } returns UserMock.mockUserRoom

        // WHEN
        viewModel.signUp(userInfoRequest)

        // THEN
        val state = viewModel.uiState.value
        assert(state.errorMessage == "El nombre de usuario ya existe")
        assert(!state.isLoading)
        assert(state.userRoom == null)

        // Verificamos que NUNCA se llamó al registro real
        coVerify(exactly = 0) { repository.signUp(any()) }
    }

    @Test
    fun `signUp - cuando el registro es exitoso, actualiza a Success`() = runTest {
        // GIVEN
        val userInfoRequest = UserRegistrationRequest(
            username = UserMock.mockUserRoom.username,
            email = UserMock.mockUserRoom.email,
            password = "password123",
            dateOfBirth = UserMock.mockUserRoom.dateOfBirth,
            address = UserMock.mockAddress,
            phoneNumber = UserMock.mockUserRoom.phoneNumber,
            receiveEmails = UserMock.mockUserRoom.receiveEmails
        )

        // Simulamos que el usuario y el email están libres en el sistema
        coEvery { repository.getUserByUsername(any()) } returns null
        coEvery { repository.getUserByEmail(any()) } returns null
        // Simulamos que el registro en el servidor devuelve con éxito nuestro usuario mockeado
        coEvery { repository.signUp(any()) } returns Result.success(UserMock.mockUserRoom)

        // WHEN - Invocamos la función con el objeto Request
        viewModel.signUp(userInfoRequest)

        // THEN
        val state = viewModel.uiState.value


        assert(state.userRoom == UserMock.mockUserRoom)
        assert(state.errorMessage == null)
        assert(!state.isLoading)
    }
}