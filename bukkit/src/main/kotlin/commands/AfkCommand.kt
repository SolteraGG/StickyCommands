/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.common.arguments.Arguments
import com.dumbdogdiner.stickyapi.common.command.ExitCode
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.util.Constants
import java.util.HashMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

val afkCommand = commandStub("afk", Constants.Descriptions.AFK, Constants.Permissions.AFK)
    .requiresPlayer()
    .onExecute { sender: CommandSender, _: Arguments?, _: HashMap<String, String>? ->
        val state = StickyCommands.plugin.playerStateManager.getPlayerState(sender as Player)
        state.setAfk(!state.isAfk, true)
        ExitCode.EXIT_SUCCESS
    }
    .onTabComplete { _, _, _ ->
        listOf()
    }
