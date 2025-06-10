package com.example.resepapp.data

import androidx.room.TypeConverter
import androidx.room.ProvidedTypeConverter // Import ini
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter // Tambahkan anotasi ini
class Converters {
    // ... isi kelas Converters yang sudah ada
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        if (list == null) return null
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toStringList(json: String?): List<String>? {
        if (json == null) return null
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(json, type)
    }
}