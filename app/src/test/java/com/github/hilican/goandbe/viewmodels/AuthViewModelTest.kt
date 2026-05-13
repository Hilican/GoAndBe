package com.github.hilican.goandbe.viewmodels

import com.github.hilican.goandbe.ui.viewmodels.AuthViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthViewModelTest {
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp()
    {
        viewModel = AuthViewModel(TestAuthRepository)
    }

    @Test
    fun isUserLoggedIn() {
    }

    @Test
    fun getCurrentUserData() {
    }

    @Test
    fun login() {
    }

    @Test
    fun logout() {
    }

    @Test
    fun signIn() {
    }

    @Test
    fun sendPasswordReset() {
    }

    @Test
    fun updateProfile() {
    }

}