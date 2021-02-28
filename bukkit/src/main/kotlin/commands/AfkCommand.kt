/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.util.Constants
import dev.jorel.commandapi.executors.PlayerCommandExecutor

val afkCommand = commandStub("afk", Constants.Permissions.AFK)
    .executesPlayer(PlayerCommandExecutor { player, _ ->
        val state = StickyCommands.plugin.playerStateManager.getPlayerState(player)
        state.setAfk(!state.isAfk, true)
        SoundUtil.sendSuccess(player)
    })
