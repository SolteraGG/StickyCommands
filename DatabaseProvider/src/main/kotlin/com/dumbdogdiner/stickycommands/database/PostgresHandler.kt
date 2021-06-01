/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.database

import com.dumbdogdiner.stickycommands.objects.Listing
import com.dumbdogdiner.stickycommands.database.tables.Listings
import com.dumbdogdiner.stickycommands.database.tables.Locations
import com.dumbdogdiner.stickycommands.database.tables.Locations.world
import com.dumbdogdiner.stickycommands.database.tables.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.dumbdogdiner.stickycommands.objects.Market
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import pw.forst.exposed.insertOrUpdate
import java.time.Instant
import java.util.*
import java.util.logging.Logger
import com.dumbdogdiner.stickycommands.database.tables.TableVars
import com.dumbdogdiner.stickycommands.utils.Constants
import com.dumbdogdiner.stickycommands.utils.Constants.DatabaseConstants
import java.util.function.Consumer


// TODO: for the love of anything document this please!!!
class PostgresHandler(val config: FileConfiguration, val logger: Logger) {
    lateinit var db: Database

    fun init(): Boolean {
        var success = true
        logger.info("[SQL] Checking SQL com.dumbdogdiner.stickycommands.database has been set up correctly...")

        TableVars.server = config.getString(Constants.SettingsPaths.SERVER, "unknown")!!
        TableVars.prefix = config.getString(Constants.DatabaseConstants.DATABASE_TABLE_PREFIX, "stickycommands_")!!

        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://${
                config.getString(DatabaseConstants.DATABASE_HOST)
            }:${
                config.getInt(DatabaseConstants.DATABASE_PORT)
            }/${
                config.getString(DatabaseConstants.DATABASE_NAME)
            }?sslmode=${config.getString(DatabaseConstants.DATABASE_USE_SSL, "allow")}"

            driverClassName = "com.dumbdogdiner.stickycommands.libs.org.postgresql.Driver"
            username = config.getString(DatabaseConstants.DATABASE_USERNAME, "postgres")!!
            password = config.getString(DatabaseConstants.DATABASE_PASSWORD)!!
            maximumPoolSize = 2
        }

        val dataSource = HikariDataSource(config)
        db = Database.connect(dataSource)

        transaction(db) {
            try {
                addLogger(ExposedLogger(logger))
                SchemaUtils.createMissingTablesAndColumns(Users, Listings, Locations)
            } catch (e: Exception) {
                logger.warning("[SQL] Failed to connect to SQL com.dumbdogdiner.stickycommands.database - invalid connection info/com.dumbdogdiner.stickycommands.database not up")
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

    fun loadUser(playerId: UUID, callback: Consumer<ResultRow>) {
        transaction(db) {
            val column = Users.select { Users.uniqueId eq playerId.toString() }.singleOrNull()
            if (column != null) {
                callback.accept(column)
            } else {
                // just in case
                Users.insert {
                    it[name] = ""
                    it[uniqueId] = playerId.toString()
                    it[ipAddress] = ""
                    it[lastSeen] = 0
                    callback.accept(it.resultedValues!!.single())
                }
            }
        }
    }

    fun getUserInfo(uniqueId: UUID) = getUserInfo(uniqueId, false)

    // Workaround for some stupid shit below.
    // FIXME find a better way.
    private fun getFirstSeen(player: Player) = transaction(db) {
        Users.select { (Users.uniqueId eq player.uniqueId.toString()) }.firstOrNull().let {
            return@transaction it?.get(Users.firstSeen) ?: player.firstPlayed
        }
    }

    fun getUserSpeed(player: Player) = transaction(db) {
        Users.select { (Users.uniqueId eq player.uniqueId.toString()) }
            .firstOrNull()?.let {
                return@transaction listOf(it[Users.walkSpeed], it[Users.flySpeed])
            }
    }

    fun setSpeed(playerId: UUID, speed: Float, isFlySpeed: Boolean) {
        transaction(db) {
            Users.update {
                if (isFlySpeed) {
                    it[flySpeed] = speed
                } else {
                    it[walkSpeed] = speed
                }
            }
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
                it[ipAddress] = player.address.address.hostAddress
                it[lastSeen] = (System.currentTimeMillis())
                it[firstSeen] = getFirstSeen(player)
                it[lastServer] = config.getString("server") ?: "unknown"
                it[isOnline] = !leaving
                it[flySpeed] = player.flySpeed
                it[walkSpeed] = player.walkSpeed
            }
            commit()
        }
    }

    fun updateLocation(player: Player) = transaction(db) {
        Locations.insertOrUpdate(Locations.uniqueId) {
            val location = player.location
            it[uniqueId] = "${player.uniqueId}"
            it[world] = "${location.world.uid}"
            it[x] = location.x
            it[y] = location.y
            it[z] = location.z
            it[pitch] = location.pitch
            it[yaw] = location.yaw
        }
    }

    fun getLocation(uniqueId: UUID) = transaction(db) {
        Locations.select { (Locations.uniqueId eq uniqueId.toString()) }
            .firstOrNull().let {
                return@transaction if (it == null || Bukkit.getWorld(UUID.fromString(it[world])) == null) null else {
                    Location(
                        Bukkit.getWorld(UUID.fromString(it[world])),
                        it[Locations.x], it[Locations.y], it[Locations.z], it[Locations.yaw], it[Locations.pitch]
                    )
                }
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
                it[sold] = config.getBoolean("auto-sell", true) // refactor this later!
            }
        }
    }


    fun getListings(market: Market, query: Query, sortBy: Listing.SortBy, page: Int, pageSize: Int): List<Listing> {
        val transactions = mutableListOf<Listing>()
        transaction(db) {
            query.limit(pageSize, ((page - 1) * pageSize).toLong())
                .orderBy(orderBy(sortBy))
                .iterator().forEach {
                    transactions.add(
                        Listing(
                            market,
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
