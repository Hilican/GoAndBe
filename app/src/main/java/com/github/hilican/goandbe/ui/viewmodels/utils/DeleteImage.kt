package com.github.hilican.goandbe.ui.viewmodels.utils

import java.io.File

fun deleteImageFromInternalStorage(path: String): Boolean {
    return try {
        val file = File(path)
        if (file.exists()) {
            file.delete() // Borra el archivo físico
        } else {
            false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}