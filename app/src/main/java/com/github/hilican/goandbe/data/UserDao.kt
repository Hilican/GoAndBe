package com.github.hilican.goandbe.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    // Insertar un usuario (si el ID ya existe, lanzará error por defecto)
    @Insert
    suspend fun insertUser(user: User)

    // Actualizar para el perfil (¡Esta es la que vas a usar ahora!)
    @Update
    suspend fun updateUser(user: User)

    // Obtener un usuario por su ID (útil para el perfil)
    @Query("SELECT * FROM users WHERE userId = :id")
    suspend fun getUserById(id: String): User?

    // Borrar un usuario (cuando cierre sesión o borre cuenta)
    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?
}