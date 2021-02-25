package com.dumbdogdiner.stickycommands.util

import com.google.gson.Gson
import org.bukkit.Location
import kotlin.reflect.KClass

object Serialization {
    private val gson = Gson()

    fun serialize(location: Location): String {
        return gson.toJson(location).toString()
    }

    fun deserialize(json: String, clazz: KClass<Location>): Any {
        return gson.fromJson(json, (clazz as Any).javaClass)
    }
}