package com.dumbdogdiner.stickycommands.player

import com.dumbdogdiner.stickycommands.api.player.PlayerStateManager
import org.bukkit.entity.Player

class StickyPlayerStateManager : PlayerStateManager {
    private val playerStates = HashMap<Player, StickyPlayerState>()

    override fun getPlayerState(player: Player): StickyPlayerState {
        return playerStates[player] ?: createPlayerState(player)
    }

    // is there a less ugly way to do this?
    // FIXME: make this look more pretty, skye pls help <3
    private fun createPlayerState(player: Player): StickyPlayerState {
        val state = StickyPlayerState(player)
        this.playerStates[player] = state
        return state
    }

    override fun isPlayerAfk(player: Player): Boolean {
        return getPlayerState(player).isAfk
    }

    /**
     * Remove a [StickyPlayerState] from the cache
     */
    fun removePlayerState(player: Player) {
        this.playerStates.remove(player)
    }
}