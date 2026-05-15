package com.github.hilican.goandbe.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    //READ
    @Query("SELECT * FROM users WHERE userId = :id")
    suspend fun getUserById(id: String): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?
    // Insertar un usuario (si el ID ya existe, lanzará error por defecto)
    @Insert
    suspend fun insertUser(user: User) : Long // <-- Devuelve positivo si salio bien, -1 si ha habido error

    // Actualizar para el perfil
    @Update
    suspend fun updateUser(user: User) : Int // <-- Devuelve numero de filas modificadas

    // Borrar un usuario
    @Delete
    suspend fun deleteUser(user: User) : Int // <-- Devuelve numero de filas modificadas
}