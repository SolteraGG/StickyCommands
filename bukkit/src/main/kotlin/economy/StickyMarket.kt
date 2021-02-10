/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.economy

import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.api.economy.Listing
import com.dumbdogdiner.stickycommands.api.economy.Market
import com.dumbdogdiner.stickycommands.models.Listings
import com.dumbdogdiner.stickycommands.util.InventoryUtil
import com.dumbdogdiner.stickycommands.util.WithPlugin
import java.util.UUID
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

// TODO implement listing cache
class StickyMarket : Market, WithPlugin {

    private fun orderBy(sortBy: Listing.SortBy): (Pair<Expression<*>, SortOrder>) = when (sortBy) {
        Listing.SortBy.DATE_ASCENDING -> Listings.date to SortOrder.ASC
        Listing.SortBy.DATE_DESCENDING -> Listings.date to SortOrder.DESC
        Listing.SortBy.PRICE_ASCENDING -> Listings.value to SortOrder.ASC
        Listing.SortBy.PRICE_DESCENDING -> Listings.value to SortOrder.ASC
        Listing.SortBy.QUANTITY -> Listings.quantity to SortOrder.ASC
        Listing.SortBy.ITEM -> Listings.item to SortOrder.ASC
    }

    private fun query(query: Query, sortBy: Listing.SortBy, page: Int, pageSize: Int): MutableList<Listing> {
        val transactions = mutableListOf<Listing>()
        transaction(this.plugin.db) {
            query.limit(pageSize, ((page - 1) * pageSize).toLong())
                .orderBy(orderBy(sortBy))
                .iterator().forEach {
                    transactions.add(
                        Listing(
                            it[Listings.id],
                            Bukkit.getOfflinePlayer(UUID.fromString(it[Listings.seller])),
                            Material.valueOf(it[Listings.item]),
                            it[Listings.value],
                            it[Listings.quantity],
                            if (it[Listings.buyer] == null) null else Bukkit.getOfflinePlayer(UUID.fromString(it[Listings.buyer]))
                        )
                    )
                }
        }
        return transactions
    }

    override fun getListings(sortBy: Listing.SortBy, page: Int, pageSize: Int): MutableList<Listing> {
        return query(Listings.selectAll(), sortBy, page, pageSize)
    }

    override fun getListingsOfType(
        material: Material,
        sortBy: Listing.SortBy,
        page: Int,
        pageSize: Int
    ): MutableList<Listing> {
        return query(Listings.select { (Listings.item eq material.toString()) }, sortBy, page, pageSize)
    }

    override fun getListingsOfPlayer(
        player: OfflinePlayer,
        sortBy: Listing.SortBy,
        page: Int,
        pageSize: Int
    ): MutableList<Listing> {
        return query(Listings.select { (Listings.seller eq player.uniqueId.toString()) }, sortBy, page, pageSize)
    }

    override fun latestId(): Int {
        var int: Int = 1
        val selectionResult = transaction(this.plugin.db) {
            Listings.selectAll().firstOrNull()
        }
        return if (selectionResult?.getOrNull(Listings.id) == null) 1 else selectionResult.getOrNull(Listings.id)!!
    }

    override fun add(listing: Listing) {

        // FIXME Better way to do this pls
        // doing this here could potentially mean that the player could somehow
        // make this execute while offline and keep the items they have sold
        // This is extremely unlikely but could in theory be possible so
        // its worth noting
        if (this.config.getBoolean("auto-sell", true) && listing.seller.isOnline) {
            val player = listing.seller as Player
            InventoryUtil.removeItems(player.inventory, listing.material, listing.quantity)
            StickyCommands.economy!!.depositPlayer(player, listing.price)
        }

        transaction(this.plugin.db) {
            Listings.insert {
                it[seller] = listing.seller.uniqueId.toString()
                it[item] = listing.material.toString()
                it[quantity] = listing.quantity
                it[value] = listing.price
                it[buyer] = if (listing.buyer == null) null else listing.buyer!!.uniqueId.toString()
            }
        }
    }

    override fun remove(listing: Listing) {
        transaction(this.plugin.db) {
            Listings.deleteWhere { (Listings.id eq listing.id) }
        }
    }
}
