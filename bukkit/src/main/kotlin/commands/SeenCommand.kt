/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.common.command.ExitCode
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import org.bukkit.entity.Player

val seenCommand = commandStub("seen", Constants.Descriptions.SEEN, Constants.Permissions.SEEN)
    .onExecute { sender, args, vars ->
        vars["syntax"] = "/seen <player>"
        if (sender is Player)
            vars.putAll(Variables().withPlayer(sender, false).get())

        args.requiredString("player")

        if (!args.exists("player"))
            return@onExecute ExitCode.EXIT_INVALID_SYNTAX

        val info = postgresHandler.getUserInfo(args.getString("player"), true)
        if (info.isEmpty()) {
            vars["target"] = args.getString("player")
            sender.sendMessage(locale.translate(Constants.LanguagePaths.PLAYER_HAS_NOT_JOINED, vars))
            return@onExecute ExitCode.EXIT_EXPECTED_ERROR
        }
        vars.putAll(info)
        vars["target_last_seen"] = (System.currentTimeMillis() - info["target_last_seen"]!!.toLong()).toString()
        vars["target_first_seen"] = (System.currentTimeMillis() - info["target_first_seen"]!!.toLong()).toString()
        sender.sendMessage(locale.translate(Constants.LanguagePaths.SEEN_MESSAGE, vars))
        ExitCode.EXIT_SUCCESS
    }
