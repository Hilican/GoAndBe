package com.github.hilican.goandbe.domain.model

data class Address(
    val street: String,       // Calle y número (ej. "Calle Mayor 15, 3°B")
    val city: String,         // Ciudad (ej. "Madrid")
    val state: String,        // Provincia / Estado / Región (ej. "Madrid")
    val zipCode: String,      // Código Postal (ej. "28013")
    val country: String,      // País (ej. "España")
    val additionalInfo: String? = null, // Datos extra opcionales (ej. "Llamar al timbre 2")
){
    fun validate(): AddressValidationError? {
        return when {
            street.isBlank() -> AddressValidationError.StreetEmpty
            street.length > 50 -> AddressValidationError.StreetTooLong
            city.isBlank() -> AddressValidationError.CityEmpty
            zipCode.length != 5 -> AddressValidationError.InvalidZipCode
            else -> null // Todo está bien
        }
    }
}

sealed class AddressValidationError(val message: String) {
    object StreetEmpty : AddressValidationError("La calle no puede estar vacía.")
    object StreetTooLong : AddressValidationError("La dirección es muy larga (máximo 50 caracteres).")
    object CityEmpty : AddressValidationError("La ciudad es obligatoria.")
    object InvalidZipCode : AddressValidationError("El código postal debe tener 5 dígitos.")
}