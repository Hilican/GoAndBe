package com.github.hilican.goandbe.data.Room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoomConverters {

    private val gson = Gson()

    // De List<String> a String (Para guardar en la base de datos)
    @TypeConverter
    fun fromImagesList(images: List<String>?): String {
        return gson.toJson(images)
    }

    // De String a List<String> (Para leer de la base de datos)
    @TypeConverter
    fun toImagesList(imagesString: String?): List<String> {
        if (imagesString == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(imagesString, listType)
    }
}