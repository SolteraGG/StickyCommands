package com.dumbdogdiner.stickycommands.util

import org.bukkit.Location
import org.bukkit.World

data class WorldLocation(var world : World, var location : Location){
    fun getWorldStr() : String{
        return world.name;
    }
}