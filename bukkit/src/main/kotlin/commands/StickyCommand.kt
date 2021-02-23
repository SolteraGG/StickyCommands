/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.entity.Player

val stickyCommand = commandStub("stickycommands", "stickycommands.stickycommands")
    .executes(CommandExecutor { sender, _ ->
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lStickyCommands &8&l» &bRunning version &av&l${plugin.description.version}"))
        if (sender is Player)
            SoundUtil.sendSuccess(sender)
    })
    .withSubcommand(
        commandStub("reload", "stickycommands.reload")
            .executes(CommandExecutor { sender, _ ->
                plugin.reloadConfig()

                // maybe move this to StickyAPI?
                locale.loadedLocales.clear()
                locale.loadAllLocales()

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lStickyCommands &8&l» &aSuccessfully reloaded config & messages!"))
                if (sender is Player)
                    SoundUtil.sendSuccess(sender)
            })
    )
