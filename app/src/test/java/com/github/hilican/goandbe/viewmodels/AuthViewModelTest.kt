package com.github.hilican.goandbe.viewmodels

import com.github.hilican.goandbe.MainDispatcherRule
import com.github.hilican.goandbe.data.User
import com.github.hilican.goandbe.domain.Address
import com.github.hilican.goandbe.domain.DTO.UserRegistrationRequest
import com.github.hilican.goandbe.domain.IAuthRepository
import com.github.hilican.goandbe.ui.viewmodels.AuthViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
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
        val validAddress = Address(
            street = "Calle Mayor 15",
            city = "Madrid",
            state = "Madrid",
            zipCode = "28013",
            country = "España"
        )

        val userInfoRequest = UserRegistrationRequest(
            username = username,
            email = "test@test.com",
            password = "password123",
            dateOfBirth = 123456789L,
            address = validAddress,
            phoneNumber = "600112233",
            receiveEmails = false
        )

        val existingUser = User(
            userId = "1",
            email = "test@test.com",
            username = username,
            dateOfBirth = 123456789L,
            createdAt = System.currentTimeMillis(),
            address = validAddress,
            phoneNumber = "600112233",
            receiveEmails = false,
        )

        coEvery { repository.getUserByUsername(username) } returns existingUser
        // WHEN
        viewModel.signUp(userInfoRequest)

        // THEN
        val state = viewModel.uiState.value
        assert(state is AuthViewModel.UiState.Error)
        assert((state as AuthViewModel.UiState.Error).message == "El nombre de usuario ya existe")

        // Verificamos que NUNCA se llamó al registro real
        coVerify(exactly = 0) { repository.signUp(any()) }
    }

    @Test
    fun `signUp - cuando el registro es exitoso, actualiza a Success`() = runTest {
        // GIVEN
        // 1. Creamos una dirección válida para superar .validate()
        val validAddress = Address(
            street = "Calle Mayor 15",
            city = "Madrid",
            state = "Madrid",
            zipCode = "28013",
            country = "España"
        )

        // 2. Creamos el objeto Request completo con datos que pasen los filtros .isBlank()
        val userInfoRequest = UserRegistrationRequest(
            username = "newGuy",
            email = "new@test.com",
            password = "password123",
            dateOfBirth = 101010L,
            address = validAddress,
            phoneNumber = "600112233",
            receiveEmails = false
        )

        // 3. Adaptamos el usuario esperado a la estructura actual de tu entidad User de Room
        val expectedUser = User(
            userId = "1",
            email = "new@test.com",
            username = "newGuy",
            dateOfBirth = 101010L,
            createdAt = System.currentTimeMillis(),
            address = validAddress,
            phoneNumber = "600112233",
            receiveEmails = false
        )

        // Simulamos que el usuario y el email están libres, y que el registro devuelve un Result exitoso
        coEvery { repository.getUserByUsername(any()) } returns null
        coEvery { repository.getUserByEmail(any()) } returns null
        coEvery { repository.signUp(any()) } returns Result.success(expectedUser)

        // WHEN - Invocamos la función con el objeto Request
        viewModel.signUp(userInfoRequest)

        // THEN
        val state = viewModel.uiState.value
        assert(state is AuthViewModel.UiState.Success)
        assert((state as AuthViewModel.UiState.Success).user == expectedUser)
    }
}