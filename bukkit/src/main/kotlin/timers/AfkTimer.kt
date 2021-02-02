/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.timers

import com.dumbdogdiner.stickyapi.common.util.NumberUtil
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.api.player.PlayerState
import com.dumbdogdiner.stickycommands.util.Variables
import java.util.TimerTask
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class AfkTimer : TimerTask() {
    protected var AFK_TIMEOUT: Int = StickyCommands.plugin.config.getInt("afk-timeout", 300)

    override fun run() {
        for (playerState in StickyCommands.plugin.playerStateManager.playerStates) {
            val state = playerState.value
            state.incrementAfkTime()
            if (state.afkTime >= AFK_TIMEOUT) {
                if (!state.isAfk) {
                    state.setAfk(!state.isAfk, true)
                } else if (exceedsPermittedTime(state, state.afkTime - AFK_TIMEOUT)) {
                    val variables = Variables(state.player, false).get()
                    variables.put("time", (state.afkTime * 1000L).toString())
                    // Bukkit doesn't like async stuff, so we have to run this 1 tick later
                    Bukkit.getScheduler().scheduleSyncDelayedTask(StickyCommands.plugin,
                        {
                            state.player.kickPlayer(StickyCommands.localeProvider!!.translate("afk.afk-kick", variables))
                        }, 1L
                    )
                }
            }
        }
    }

    private fun exceedsPermittedTime(state: PlayerState, time: Int): Boolean {
        val player: Player? = state.player
        if (player == null) {
            System.err.println("Error in exceedsPermissionTime: Player was null. Defaulting to false.")
            return false
        }
        if (state.isHidden || state.isVanished) return false
        for (permission in player.effectivePermissions) {
            if (!permission.permission
                    .contains("stickycommands.afk.autokick")
            ) continue // We don't care about other permissions
            val afkArr = permission.permission.split(".")
            val afkPerm = if (afkArr.size == 4) afkArr[3] else "unlimited" // We only care about the time!
            if (!NumberUtil.isNumeric(afkPerm) || afkPerm == "unlimited") return false
            val permTime = afkPerm.toInt()
            return permTime <= time
        }
        return false
    }
}
