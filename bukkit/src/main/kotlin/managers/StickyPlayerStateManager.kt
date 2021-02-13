/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.managers

import com.dumbdogdiner.stickycommands.api.managers.PlayerStateManager
import com.dumbdogdiner.stickycommands.api.player.PlayerState
import com.dumbdogdiner.stickycommands.api.util.WithApi
import com.dumbdogdiner.stickycommands.database.tables.Users
import com.dumbdogdiner.stickycommands.player.StickyPlayerState
import com.dumbdogdiner.stickycommands.util.WithPlugin
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import pw.forst.exposed.insertOrUpdate

class StickyPlayerStateManager : PlayerStateManager, WithApi, WithPlugin {
    companion object {
        val playerStateManager = StickyPlayerStateManager()
    }
    private val playerStates = HashMap<Player, PlayerState>()

    override fun getPlayerState(player: Player): PlayerState {
        return playerStates[player] ?: createPlayerState(player)
    }

    override fun getPlayerStates(): HashMap<Player, PlayerState> {
        return this.playerStates
    }

    override fun createPlayerState(player: Player): PlayerState {
        val state = StickyPlayerState(player)
        this.playerStates[player] = state
        update(player, false)
        return state
    }

    override fun isPlayerAfk(player: Player): Boolean {
        return getPlayerState(player).isAfk
    }

    override fun isPlayerHidden(player: Player): Boolean {
        return getPlayerState(player).isHidden
    }

    /**
     * Remove a [StickyPlayerState] from the cache
     */
    fun removePlayerState(player: Player) {
        this.playerStates.remove(player)
        update(player, true)
    }

    /**
     * Update the database entry for this player
     * @param player to update
     */
    // I don't see why this would be used outside of this class.
    private fun update(player: Player, leaving: Boolean) {
        transaction(plugin.db) {
            // FIXME WHY DOES THIS UPDATE A COLUMN NOT LISTED BELOW?!
            // firstSeen updates when they join, this should not happen.
            Users.insertOrUpdate(Users.uniqueId) {
                it[uniqueId] = player.uniqueId.toString()
                it[ipAddress] = player.address.address.toString()
                it[lastSeen] = (System.currentTimeMillis() / 1000L)
                it[lastServer] = plugin.config.getString("server") ?: "unknown"
                it[isOnline] = !leaving
            }
            commit()
        }
    }
}
