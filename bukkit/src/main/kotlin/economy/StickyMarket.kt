/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.economy

import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.WithPlugin
import com.dumbdogdiner.stickycommands.api.economy.Listing
import com.dumbdogdiner.stickycommands.api.economy.Market
import com.dumbdogdiner.stickycommands.database.tables.Listings
import com.dumbdogdiner.stickycommands.util.InventoryUtil
import event.ListingCreateEvent
import event.PowertoolExecuteEvent
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

// TODO implement listing cache
class StickyMarket : Market, WithPlugin {

    private fun query(query: Query, sortBy: Listing.SortBy, page: Int, pageSize: Int): List<Listing> {
        return this.plugin.postgresHandler.getListings(query, sortBy, page, pageSize)
    }

    override fun getListings(sortBy: Listing.SortBy, page: Int, pageSize: Int): List<Listing> {
        return query(Listings.selectAll(), sortBy, page, pageSize)
    }

    override fun getListingsOfType(
        material: Material,
        sortBy: Listing.SortBy,
        page: Int,
        pageSize: Int
    ): List<Listing> {
        return query(Listings.select { (Listings.item eq material.toString()) }, sortBy, page, pageSize)
    }

    override fun getListingsOfPlayer(
        player: OfflinePlayer,
        sortBy: Listing.SortBy,
        page: Int,
        pageSize: Int
    ): List<Listing> {
        return query(Listings.select { (Listings.seller eq player.uniqueId.toString()) }, sortBy, page, pageSize)
    }

    override fun latestId(): Int {
        val selectionResult = transaction(this.plugin.postgresHandler.db) {
            Listings.selectAll().firstOrNull()
        }
        return if (selectionResult?.getOrNull(Listings.id) == null) 1 else selectionResult[Listings.id]
    }

    override fun getListingCount(): Long {
        var count = 0L
        transaction(this.plugin.postgresHandler.db) {
            count = Listings.selectAll().count()
        }
        return count
    }

    override fun add(listing: Listing) {
        val event = ListingCreateEvent(listing)
        this.callBukkitEvent(event)
        if (event.isCancelled)
            return

        if (StickyCommands.plugin.config.getBoolean("auto-sell", true) && listing.seller.isOnline) {
            InventoryUtil.removeItems((listing.seller as Player).inventory, listing.material, listing.quantity)
            StickyCommands.economy!!.depositPlayer(listing.seller, listing.price)
        }
        this.plugin.postgresHandler.addListing(listing)
    }

    override fun remove(listing: Listing) {
        transaction(this.plugin.postgresHandler.db) {
            Listings.deleteWhere { (Listings.id eq listing.id) }
        }
    }
}
