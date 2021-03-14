/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.util

import com.dumbdogdiner.stickyapi.common.util.StringUtil
import com.dumbdogdiner.stickyapi.common.util.TimeUtil
import com.dumbdogdiner.stickycommands.WithPlugin
import com.dumbdogdiner.stickycommands.api.economy.Listing
import kotlin.math.round
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory

/**
 * Utility class for getting a variables map with all the information about a player
 */
class Variables() : WithPlugin {

    private val variables = HashMap<String, String>()

    /**
     * Get placeholders for a player object
     */
    fun withPlayer(target: Player, isTarget: Boolean): Variables {
        val prefix = if (isTarget) "target" else "player"
        withOfflinePlayer(target, isTarget)
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
        variables["${prefix}_fly_speed"] = target.flySpeed.toString()
        variables["${prefix}_walk_speed"] = target.walkSpeed.toString()
        variables["${prefix}_flying"] = target.isFlying.toString()
        variables["${prefix}_speed"] = if (target.isFlying) target.flySpeed.toString() else target.walkSpeed.toString()
        variables["${prefix}_allow_flight"] = target.allowFlight.toString()
        return this
    }

    /**
     * Get placeholders for an player object
     */
    fun withOfflinePlayer(target: OfflinePlayer, isTarget: Boolean): Variables {
        val prefix = if (isTarget) "target" else "player"
        variables[prefix] = target.name ?: "unknown"
        variables["${prefix}_uuid"] = target.uniqueId.toString()
        variables.putAll(this.plugin.postgresHandler.getUserInfo(target.uniqueId, isTarget))
        return this
    }

    /**
     * Get placeholders for a listing object
     */
    fun withListing(listing: Listing): Variables {
        variables["worth"] = (listing.price).toString()
        variables["amount"] = (listing.quantity).toString()
        variables["item"] = StringUtil.capitaliseSentence(listing.material.toString().replace("_", " "))
        variables["item_enum"] = listing.material.toString()
        variables["date"] = (listing.listedAt.time).toString()
        variables["log_player"] = listing.seller.name.toString()
        variables["saleid"] = listing.id.toString()
        variables["amount"] = listing.quantity.toString()
        variables["price"] = (listing.price).toString()
        variables["short_date"] = TimeUtil.significantDurationString(System.currentTimeMillis() - listing.listedAt.time / 1000L) // dumb but whatever
        variables["date_duration"] = TimeUtil.expirationTime(System.currentTimeMillis() - listing.listedAt.time / 1000L)
        return this
    }

    /**
     * Get placeholders for a listing object
     */
    fun withListing(listing: Listing, inventory: PlayerInventory): Variables {
        variables["single_worth"] = (listing.price / listing.quantity).toString()
        variables["hand_worth"] = ((listing.price / listing.quantity) * (inventory.itemInMainHand.amount)).toString()
        variables["inventory_worth"] = ((listing.price / listing.quantity) * (InventoryUtil.count(inventory, listing.material))).toString()
        withListing(listing)
        return this
    }

    /**
     * Get placeholders for a location object
     */
    fun withLocation(location: Location): Variables {
        val x = location.x.round(2)
        val y = location.y.round(2)
        val z = location.z.round(2)
        variables["location"] = "$x, $y, $z"
        variables["location_x"] = "$x"
        variables["location_y"] = "$y"
        variables["location_z"] = "$z"
        variables["world"] = location.world.name
        variables["pitch"] = location.pitch.toString()
        variables["yaw"] = location.yaw.toString()
        return this
    }

    fun withSender(sender: CommandSender): Variables {
        variables["name"] = sender.name
        return this
    }

    /**
     * Get the map of placeholders
     */
    fun get(): HashMap<String, String> {
        return this.variables
    }

    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }
}
