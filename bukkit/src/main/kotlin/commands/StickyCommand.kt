/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.common.command.ExitCode
import org.bukkit.ChatColor

val stickyCommand = commandStub("stickycommands", "StickyCommands configuration command", "stickycommands.stickycommands")
    .onTabComplete { _, _, args ->
        listOf("reload")
    }
    .onExecute { sender, args, vars ->
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lStickyCommands &8&l» &bRunning version &av&l${plugin.description.version}"))
        ExitCode.EXIT_SUCCESS
    }
    .subCommand(
        commandStub("reload", "Reload StickyCommands", "stickycommands.reload")
            .onExecute { sender, args, vars ->
                plugin.reloadConfig()

                // maybe move this to StickyAPI?
                locale.loadedLocales.clear()
                locale.loadAllLocales()

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lStickyCommands &8&l» &aSuccessfully reloaded config & messages!"))
                ExitCode.EXIT_SUCCESS
            }
    )
