package com.mobile.base.utils.extensions

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

inline fun <reified T> String.fromJsonToList(): List<T> {
    return try {
        Gson().fromJson(this, object : TypeToken<List<T>>() {}.type)
    } catch (e: JsonSyntaxException) {
        emptyList()
    }
}