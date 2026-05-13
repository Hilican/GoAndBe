package com.github.hilican.goandbe.domain.DTO

data class UserRegistrationRequest(
    val username: String,
    val email: String,
    val password: String,
    val dateOfBirth: Long
)