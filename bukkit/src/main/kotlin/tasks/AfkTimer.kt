/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.timers

import com.dumbdogdiner.stickyapi.common.util.NumberUtil
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.api.player.PlayerState
import com.dumbdogdiner.stickycommands.tasks.StickyTask
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class AfkTimer : StickyTask(1000L, 1000L) {
    private var AFK_TIMEOUT: Int = StickyCommands.plugin.config.getInt(Constants.SettingsPaths.AFK_TIMEOUT, 300)

    override fun run() {
        for (playerState in StickyCommands.plugin.playerStateManager.playerStates) {
            val state = playerState.value
            state.incrementAfkTime()
            if (state.afkTime < AFK_TIMEOUT)
                return

            if (!state.isAfk)
                state.setAfk(!state.isAfk, true)

            if (exceedsPermittedTime(state, state.afkTime - AFK_TIMEOUT)) {
                val variables = Variables().withPlayer(state.player, false).get()
                variables["time"] = (state.afkTime * 1e3).toString()
                // Bukkit doesn't like async stuff, so we have to run this 1 tick later
                Bukkit.getScheduler().scheduleSyncDelayedTask(StickyCommands.plugin,
                    {
                        state.player.kickPlayer(StickyCommands.localeProvider!!.translate(Constants.LanguagePaths.AFK_KICK, variables))
                    }, 1L
                )
            }
        }
    }

    // Maybe change this up a bit and move it to stickyapi?
    // this can have some nice uses
    private fun exceedsPermittedTime(state: PlayerState, time: Int): Boolean {
        val player: Player = state.player
        if (state.isHidden || state.isVanished)
            return false

        player.effectivePermissions.forEach {
            if (it.permission.contains("stickycommands.afk.autokick")) {
                // We don't care about other permissions
                val split = it.permission.split(".")
                val permittedTime = if (split.size == 4) split[3] else "unlimited" // We only care about the time!
                if (!NumberUtil.isNumeric(permittedTime) || permittedTime == "unlimited") return false
                val permTime = permittedTime.toInt()
                return permTime <= time
            }
        }
        return false
    }
}
