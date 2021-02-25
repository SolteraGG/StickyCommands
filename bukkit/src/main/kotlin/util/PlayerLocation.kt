@file:JvmName("PlayerLocation")
package com.dumbdogdiner.stickycommands.util

import com.google.gson.Gson
import org.bukkit.Location
import org.bukkit.entity.Player
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaClass

data class PlayerLocation(var x : Double, var y : Double, var z : Double, var pitch : Float, var yaw : Float, var world : String) {
    constructor(location : Location) : this(location.x, location.y, location.z, location.pitch, location.yaw, location.world.name){

    }

    fun serialize(): String {
        return gson.toJson(this).toString()
    }

    companion object {
        private val gson  = Gson()
        fun deserialize(json : String) : PlayerLocation{
            return gson.fromJson(json, (PlayerLocation::class as Any).javaClass) as PlayerLocation
        }
    }
}