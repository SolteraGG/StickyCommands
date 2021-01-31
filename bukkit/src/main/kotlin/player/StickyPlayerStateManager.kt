/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.player

import com.dumbdogdiner.stickycommands.api.player.PlayerState
import com.dumbdogdiner.stickycommands.api.player.PlayerStateManager
import com.dumbdogdiner.stickycommands.api.util.WithApi
import org.bukkit.entity.Player

class StickyPlayerStateManager : PlayerStateManager, WithApi {
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
    }
}
