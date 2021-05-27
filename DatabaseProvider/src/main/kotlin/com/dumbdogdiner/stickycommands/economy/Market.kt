/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.economy

import com.dumbdogdiner.stickycommands.database.PostgresHandler
import com.dumbdogdiner.stickycommands.database.tables.Listings
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

// TODO implement listing cache?? (is this actually necessary because I don't think so)
class Market (val database : PostgresHandler){
    // I truely, deeply lothe companion objects but kotlin is exceedingly stupid
    // and won't let you do constants in classes. At least it doesnt make it too dumb
    // to use in java
    companion object {
        /**
         * Maximum length of a page
         */
        const val PAGE_MAX_SIZE: Int = 10
    }

    // How do i do this best? I think i need ot pass it in...
    private fun query(query: Query, sortBy: Listing.SortBy, page: Int, pageSize: Int): List<Listing> {
        return database.getListings(query, sortBy, page, pageSize)
    }

    /**
     * Get the number of listings on the market
     * @return The number of listings as a [Long]
     */
    fun getListings(sortBy: Listing.SortBy, page: Int = 1, pageSize: Int = PAGE_MAX_SIZE): List<Listing> {
        return query(Listings.selectAll(), sortBy, page, pageSize)
    }

    fun getListingsOfType(material: Material, sortBy: Listing.SortBy, page: Int = 1, pageSize: Int = PAGE_MAX_SIZE): List<Listing> {
        return query(Listings.select { (Listings.item eq material.toString()) }, sortBy, page, pageSize)
    }

    /**
     * Get the listings that a player has listed
     *
     * @param player   to get listings of
     * @param sortBy
     * @param page     to get
     * @param pageSize per page
     *
     * @return A list of listings belonging to a player
     */
    fun getListingsOfPlayer(
        player: OfflinePlayer,
        sortBy: Listing.SortBy = Listing.SortBy.DATE_ASCENDING,
        page: Int = 1,
        pageSize: Int = PAGE_MAX_SIZE
    ): List<Listing> {
        return query(Listings.select { (Listings.seller eq player.uniqueId.toString()) }, sortBy, page, pageSize)
    }

    /**
     * @return Gets the latest ID??
     */
    fun latestId(): Int {
        val selectionResult = transaction(database.db) {
            Listings.selectAll().firstOrNull()
        }
        return if (selectionResult?.getOrNull(Listings.id) == null) 1 else selectionResult[Listings.id]
    }

    /**
     * Get the number of listings on the market
     *
     * @return The number of listings as a [Long]
     */
    fun getListingCount(): Long {
        var count = 0L
        transaction(database.db) {
            count = Listings.selectAll().count()
        }
        return count
    }

    /**
     * Add a listing to the market
     *
     * @param listing to list
     */
    fun add(listing: Listing) {
        database.addListing(listing)
    }

    /**
     * Remove a listing from the market
     */
    fun remove(listing: Listing) {
        transaction(database.db) {
            Listings.deleteWhere { (Listings.id eq listing.id) }
        }
    }
}
