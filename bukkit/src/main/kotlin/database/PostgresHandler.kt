/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.database

import com.dumbdogdiner.stickycommands.WithPlugin
import com.dumbdogdiner.stickycommands.api.economy.Listing
import com.dumbdogdiner.stickycommands.database.tables.Listings
import com.dumbdogdiner.stickycommands.database.tables.Users
import com.dumbdogdiner.stickycommands.util.Constants
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.time.Instant
import java.util.Date
import java.util.UUID
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.LowerCase
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import pw.forst.exposed.insertOrUpdate

class PostgresHandler() : WithPlugin {
    lateinit var db: Database

    fun init(): Boolean {
        var success = true
        this.logger.info("[SQL] Checking SQL database has been set up correctly...")

        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://${
                config.getString(Constants.SettingsPaths.DATABASE_HOST)
            }:${
                config.getInt(Constants.SettingsPaths.DATABASE_PORT)
            }/${
                config.getString(Constants.SettingsPaths.DATABASE_DATABASE)
            }?sslmode=${config.getString(Constants.SettingsPaths.DATABASE_USE_SSL, "disabled")}"

            driverClassName = "com.dumbdogdiner.stickycommands.libs.org.postgresql.Driver"
            username = config.getString(Constants.SettingsPaths.DATABASE_USERNAME, "postgres")!!
            password = config.getString(Constants.SettingsPaths.DATABASE_PASSWORD)!!
            maximumPoolSize = 2
        }

        val dataSource = HikariDataSource(config)
        db = Database.connect(dataSource)

        transaction(db) {
            try {
                addLogger(ExposedLogger())
                SchemaUtils.createMissingTablesAndColumns(Users, Listings)
            } catch (e: Exception) {
                logger.warning("[SQL] Failed to connect to SQL database - invalid connection info/database not up")
                success = false
            }
        }
        return dataSource.isRunning || success
    }

    /**********************
        BEGIN USER UTILS
     **********************/

    fun getUserInfo(username: String, isTarget: Boolean): Map<String, String> {
        return transaction(db) {
            val result = Users.select { LowerCase(Users.name) eq username.toLowerCase() }.limit(1).firstOrNull()
            return@transaction (if (result == null) mapOf() else getUserInfo(UUID.fromString(result[Users.uniqueId]), isTarget))
        }
    }

    fun getUserInfo(uniqueId: UUID, isTarget: Boolean): Map<String, String> {
        val info = mutableMapOf<String, String>()
        val prefix = if (isTarget) "target" else "player"
        transaction(db) {
            Users.select { Users.uniqueId eq uniqueId.toString() }
                .forEach {
                    info[prefix] = it[Users.name]
                    info["${prefix}_uuid"] = it[Users.uniqueId].toString()
                    info["${prefix}_online"] = it[Users.isOnline].toString()
                    info["${prefix}_first_seen"] = it[Users.firstSeen].toString()
                    info["${prefix}_last_seen"] = it[Users.lastSeen].toString()
                    info["${prefix}_last_server"] = it[Users.lastServer].toString()
                    info["${prefix}_ipaddress"] = it[Users.ipAddress].toString()
                    info["${prefix}_fly_speed"] = (it[Users.flySpeed] * 10).toString()
                    info["${prefix}_walk_speed"] = (it[Users.walkSpeed] * 10).toString()
                }
        }
        return info
    }

    fun getUserInfo(uniqueId: UUID): Map<String, String> {
        return getUserInfo(uniqueId, false)
    }

    // Workaround for some stupid shit below.
    // FIXME find a better way.
    private fun getFirstSeen(player: Player): Long {
        var time: Long? = null
        transaction(db) {
            Users.select { (Users.uniqueId eq player.uniqueId.toString()) }.firstOrNull()?.let {
                time = it[Users.firstSeen]
            }
        }
        return time ?: (player.firstPlayed)
    }

    fun getUserSpeed(player: Player) = transaction(db) {
        Users.select { (Users.uniqueId eq player.uniqueId.toString()) }
            .firstOrNull()?.let {
                return@transaction listOf(it[Users.walkSpeed], it[Users.flySpeed])
            }
    }

    fun updateUser(player: Player, leaving: Boolean) {
        if (!leaving) {
            val speed = getUserSpeed(player)
            player.walkSpeed = (speed?.get(0) ?: player.walkSpeed)
            player.flySpeed = (speed?.get(1) ?: player.flySpeed)
        }

        transaction(db) {
            // FIXME WHY DOES THIS UPDATE A COLUMN NOT LISTED BELOW?!
            // firstSeen updates when they join, this should not happen.
            Users.insertOrUpdate(Users.uniqueId) {
                it[name] = player.name
                it[uniqueId] = player.uniqueId.toString()
                it[ipAddress] = player.address.address.toString()
                it[lastSeen] = (System.currentTimeMillis())
                it[firstSeen] = getFirstSeen(player)
                it[lastServer] = plugin.config.getString("server") ?: "unknown"
                it[isOnline] = !leaving
                it[flySpeed] = player.flySpeed
                it[walkSpeed] = player.walkSpeed
            }
            commit()
        }
    }

    /**********************
        END USER UTILS
     **********************/

    /**********************
       BEGIN MARKET UTILS
     **********************/

    private fun orderBy(sortBy: Listing.SortBy): (Pair<Expression<*>, SortOrder>) = when (sortBy) {
        Listing.SortBy.DATE_ASCENDING -> Listings.listedAt to SortOrder.ASC
        Listing.SortBy.DATE_DESCENDING -> Listings.listedAt to SortOrder.DESC
        Listing.SortBy.PRICE_ASCENDING -> Listings.value to SortOrder.ASC
        Listing.SortBy.PRICE_DESCENDING -> Listings.value to SortOrder.DESC
        Listing.SortBy.QUANTITY -> Listings.quantity to SortOrder.ASC
        Listing.SortBy.ITEM -> Listings.item to SortOrder.ASC
    }

    fun addListing(listing: Listing) {
        transaction(db) {
            Listings.insert {
                it[seller] = listing.seller.uniqueId.toString()
                it[item] = listing.material.toString()
                it[quantity] = listing.quantity
                it[value] = listing.price
                it[buyer] = if (listing.buyer == null) null else listing.buyer!!.uniqueId.toString()
                it[sold] = plugin.config.getBoolean("auto-sell", true) // refactor this later!
            }
        }
    }

    fun getListings(query: Query, sortBy: Listing.SortBy, page: Int, pageSize: Int): List<Listing> {
        val transactions = mutableListOf<Listing>()
        transaction(db) {
            query.limit(pageSize, ((page - 1) * pageSize).toLong())
                .orderBy(orderBy(sortBy))
                .iterator().forEach {
                    transactions.add(
                        Listing(
                            it[Listings.id],
                            Bukkit.getOfflinePlayer(UUID.fromString(it[Listings.seller])),
                            Material.valueOf(it[Listings.item]),
                            (it[Listings.value] / it[Listings.quantity]),
                            it[Listings.quantity],
                            if (it[Listings.buyer] == null) null else Bukkit.getOfflinePlayer(UUID.fromString(it[Listings.buyer])),
                            Date.from(Instant.ofEpochSecond(it[Listings.listedAt]))
                        )
                    )
                }
        }
        return transactions
    }

    /**********************
       END MARKET UTILS
     **********************/
}
