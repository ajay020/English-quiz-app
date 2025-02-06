package com.example.quizzybee.utils

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class Converters {
    private val moshi: Moshi = Moshi.Builder().build()
    private val listType = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter = moshi.adapter<List<String>>(listType)

    @TypeConverter
    fun fromString(value: String): List<String> = adapter.fromJson(value) ?: emptyList()

    @TypeConverter
    fun fromList(list: List<String>): String = adapter.toJson(list)
}
