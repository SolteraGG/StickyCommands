/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.command.BukkitCommandBuilder
import com.dumbdogdiner.stickyapi.common.command.ExitCode
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.api.economy.Listing
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import org.bukkit.entity.Player

object WorthCommand {
    private val locale = StickyCommands.localeProvider!!
    private val worthTable = StickyCommands.plugin.worthTable

    val command = BukkitCommandBuilder("worth")
        .permission(Constants.Permissions.WORTH)
        .description(Constants.Descriptions.WORTH)
        .requiresPlayer()
        .playSound()

        .onExecute { sender, args, vars ->
            val player = sender as Player
            vars.putAll(Variables().withPlayer(player, false).get())

            val stack = player.inventory.itemInMainHand
            if (!worthTable.isSellable(stack)) {
                sender.sendMessage(locale.translate(Constants.LanguagePaths.CANNOT_SELL, vars))
                return@onExecute ExitCode.EXIT_EXPECTED_ERROR
            }

            val listing = Listing(player, stack.type, worthTable.getWorth(stack), stack.amount)
            vars.putAll(Variables().withListing(listing, player.inventory).get())

            sender.sendMessage(locale.translate(Constants.LanguagePaths.WORTH_MESSAGE, vars))
            ExitCode.EXIT_SUCCESS
        }

        .onError { exitCode, sender, _, vars ->
            when (exitCode) {
                ExitCode.EXIT_PERMISSION_DENIED -> sender.sendMessage(locale.translate(Constants.LanguagePaths.NO_PERMISSION, vars))
                ExitCode.EXIT_MUST_BE_PLAYER -> sender.sendMessage(locale.translate(Constants.LanguagePaths.MUST_BE_PLAYER, vars))
                ExitCode.EXIT_ERROR -> sender.sendMessage(locale.translate(Constants.LanguagePaths.SERVER_ERROR, vars))
                else -> ""
            }
        }
}
