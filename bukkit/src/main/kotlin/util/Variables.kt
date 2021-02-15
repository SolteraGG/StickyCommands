/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.util

import com.dumbdogdiner.stickyapi.common.util.StringUtil
import com.dumbdogdiner.stickyapi.common.util.TimeUtil
import com.dumbdogdiner.stickycommands.api.economy.Listing
import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory

/**
 * Utility class for getting a variables map with all the information about a player
 */
class Variables() {

    private val variables = HashMap<String, String>()

    fun withPlayer(target: Player, isTarget: Boolean): Variables {
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
        return this
    }

    fun withListing(listing: Listing): Variables {
        variables["worth"] = (listing.price).toString()
        variables["amount"] = (listing.quantity).toString()
        variables["item"] = StringUtil.capitaliseSentence(listing.material.toString().replace("_", " "))
        variables["item_enum"] = listing.material.toString()
        variables["date"] = listing.listedAt.time.toString()
        variables["log_player"] = listing.seller.name.toString()
        variables["saleid"] = listing.id.toString()
        variables["amount"] = listing.quantity.toString()
        variables["price"] = (listing.price).toString()
        variables["short_date"] = TimeUtil.significantDurationString(System.currentTimeMillis() - listing.listedAt.time) // dumb but whatever
        variables["date_duration"] = TimeUtil.expirationTime(System.currentTimeMillis() - listing.listedAt.time)
        return this
    }

    fun withListing(listing: Listing, inventory: PlayerInventory): Variables {
        variables["single_worth"] = (listing.price / listing.quantity).toString()
        variables["hand_worth"] = ((listing.price / listing.quantity) * (inventory.itemInMainHand.amount)).toString()
        variables["inventory_worth"] = ((listing.price / listing.quantity) * (InventoryUtil.count(inventory, listing.material))).toString()
        withListing(listing)
        return this
    }

    fun get(): HashMap<String, String> {
        return this.variables
    }
}
