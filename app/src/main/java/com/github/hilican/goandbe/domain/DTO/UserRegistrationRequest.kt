package com.github.hilican.goandbe.domain.DTO

import com.github.hilican.goandbe.domain.model.Address

data class UserRegistrationRequest(
    val username: String,
    val email: String,
    val password: String,
    val dateOfBirth: Long,
    val address: Address,
    val phoneNumber : String,
    val receiveEmails : Boolean
)