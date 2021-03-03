/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.util.sticky

import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.api.util.Placeholders
import com.dumbdogdiner.stickycommands.api.util.WithApi
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

class StickyPlaceholders : Placeholders, WithApi, PlaceholderExpansion() {
    companion object {
        val instance = StickyPlaceholders()
    }

    override fun persist(): Boolean {
        return true
    }

    override fun getAuthor(): String {
        return StickyCommands.plugin.description.authors.toString()
    }

    override fun getIdentifier(): String {
        return StickyCommands.plugin.name.toLowerCase()
    }

    override fun getVersion(): String {
        return StickyCommands.plugin.description.version
    }

    override fun onPlaceholderRequest(player: Player, identifier: String): String? {
        return if (identifier == "afk") {
            if (this.playerStateManager.getPlayerState(player).isAfk) "&8[AFK]" else ""
        } else null
    }
}
