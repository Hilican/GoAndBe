package com.github.hilican.goandbe.ui.viewmodels

import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Hilt se asegura de que solo exista una instancia en toda la app
class SessionManager @Inject constructor() {
    // Aquí guardarás el ID cuando el usuario haga Login/SignUp
    var currentUserId: String = ""
}