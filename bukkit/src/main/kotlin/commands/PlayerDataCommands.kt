/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil
import com.dumbdogdiner.stickyapi.common.util.StringUtil
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

val seenCommand = commandStub("seen", Constants.Permissions.SEEN)
    .withArguments(StringArgument("player"))
    .executesPlayer(PlayerCommandExecutor { sender, args ->
        PlayerDataCommands.execute(sender, args, false)
    })

val whoisCommand = commandStub("whois", Constants.Permissions.WHOIS)
    .withArguments(StringArgument("player"))
    .executesPlayer(PlayerCommandExecutor { sender, args ->
        PlayerDataCommands.execute(sender, args, true)
    })

object PlayerDataCommands {
    internal fun execute(sender: CommandSender, args: Array<Any>, whois: Boolean) {
        val vars = Variables().withPlayer(sender as Player, false).get()
        vars["syntax"] = "/whois <player>"
        val target = args[0].toString()

        val info = postgresHandler.getUserInfo(target, true)
        if (info.isEmpty()) {
            vars["target"] = target
            sender.sendMessage(locale.translate(Constants.LanguagePaths.PLAYER_HAS_NOT_JOINED, vars))
            SoundUtil.sendError(sender)
            return
        }
        vars.putAll(info)
        vars["target_last_seen"] = (System.currentTimeMillis() - info["target_last_seen"]!!.toLong()).toString()
        vars["target_first_seen"] = (System.currentTimeMillis() - info["target_first_seen"]!!.toLong()).toString()

        val ip = info["target_ipaddress"] ?: "unknown"
        vars["target_ipaddress"] = if (sender.hasPermission(Constants.Permissions.WHOIS_IP)) info["target_ipaddress"] ?: "unknown" else StringUtil.censorWord(ip)

        sender.sendMessage(locale.translate(if (whois) Constants.LanguagePaths.WHOIS_MESSAGE else Constants.LanguagePaths.SEEN_MESSAGE, vars))
        SoundUtil.sendSuccess(sender)
        return
    }
}
