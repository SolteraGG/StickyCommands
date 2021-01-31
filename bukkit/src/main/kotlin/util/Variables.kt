/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.util

import org.bukkit.entity.Player

/**
 * Utility class for getting a variables map with all the information about a player
 */
class Variables(private val target: Player, private val isTarget: Boolean) {

    private val variables = HashMap<String, String>()

    init {
        val prefix = if (isTarget) "target" else "player"
        variables[prefix] = target.name
        variables["${prefix}_uuid"] = target.uniqueId.toString()
        variables["${prefix}_exp"] = target.exp.toString()
        variables["${prefix}_level"] = target.level.toString()
        variables["${prefix}_all_exp"] = target.totalExperience.toString()
        variables["${prefix}_world"] = target.world.name
        variables["${prefix}_health"] = target.health.toString()
        variables["${prefix}_hunger"] = target.foodLevel.toString()
        variables["${prefix}_saturation"] = target.saturation.toString()
        variables["${prefix}_exhaustion"] = target.exhaustion.toString()
        variables["${prefix}_location"] = "${target.location.x}, ${target.location.y}, ${target.location.z}"
        variables["${prefix}_location_x"] = target.location.x.toString()
        variables["${prefix}_location_y"] = target.location.y.toString()
        variables["${prefix}_location_z"] = target.location.z.toString()
    }

    fun get(): HashMap<String, String> {
        return this.variables
    }
}
