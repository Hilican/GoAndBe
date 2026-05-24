package com.github.hilican.goandbe.viewmodel.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        // Creamos un nombre único para el archivo para que no se sobreescriban
        val fileName = "trip_img_${UUID.randomUUID()}.jpg"

        // Accedemos a la carpeta privada de la app (no requiere permisos en el AndroidManifest)
        val file = File(context.filesDir, fileName)

        // Abrimos los streams para copiar el contenido
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        // Devolvemos la ruta absoluta del archivo guardado
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}