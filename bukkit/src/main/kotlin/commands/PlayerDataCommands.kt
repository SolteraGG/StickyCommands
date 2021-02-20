/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.common.arguments.Arguments
import com.dumbdogdiner.stickyapi.common.command.ExitCode
import com.dumbdogdiner.stickyapi.common.util.StringUtil
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

val seenCommand = commandStub("seen", Constants.Descriptions.SEEN, Constants.Permissions.SEEN)
    .onExecute { sender, args, vars ->
        return@onExecute PlayerDataCommands.execute(sender, args, vars, false)
    }

val whoisCommand = commandStub("whois", Constants.Descriptions.SEEN, Constants.Permissions.SEEN)
    .onExecute { sender, args, vars ->
        return@onExecute PlayerDataCommands.execute(sender, args, vars, true)
    }

object PlayerDataCommands {
    internal fun execute(sender: CommandSender, args: Arguments, vars: HashMap<String, String>, whois: Boolean): ExitCode {
        vars["syntax"] = "/whois <player>"
        if (sender is Player)
            vars.putAll(Variables().withPlayer(sender, false).get())

        args.requiredString("player")

        if (!args.exists("player"))
            return ExitCode.EXIT_INVALID_SYNTAX

        val info = postgresHandler.getUserInfo(args.getString("player"), true)
        if (info.isEmpty()) {
            vars["target"] = args.getString("player")
            sender.sendMessage(locale.translate(Constants.LanguagePaths.PLAYER_HAS_NOT_JOINED, vars))
            return ExitCode.EXIT_EXPECTED_ERROR
        }
        vars.putAll(info)
        vars["target_last_seen"] = (System.currentTimeMillis() - info["target_last_seen"]!!.toLong()).toString()
        vars["target_first_seen"] = (System.currentTimeMillis() - info["target_first_seen"]!!.toLong()).toString()

        val ip = info["target_ipaddress"] ?: "unknown"
        vars["target_ipaddress"] = if (sender.hasPermission(Constants.Permissions.WHOIS_IP)) info["target_ipaddress"] ?: "unknown" else StringUtil.censorWord(ip)

        sender.sendMessage(locale.translate(if (whois) Constants.LanguagePaths.WHOIS_MESSAGE else Constants.LanguagePaths.SEEN_MESSAGE, vars))
        return ExitCode.EXIT_SUCCESS
    }
}
