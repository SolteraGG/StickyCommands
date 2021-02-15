/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.common.command.ExitCode
import com.dumbdogdiner.stickycommands.api.economy.Listing
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import org.bukkit.entity.Player

val worthCommand = commandStub("worth", Constants.Descriptions.WORTH, Constants.Permissions.WORTH)
    .requiresPlayer()
    .onExecute { sender, args, vars ->
        sender as Player
        vars.putAll(Variables().withPlayer(sender, false).get())

        val stack = sender.inventory.itemInMainHand
        if (!worthTable.isSellable(stack)) {
            sender.sendMessage(locale.translate(Constants.LanguagePaths.CANNOT_SELL, vars))
            return@onExecute ExitCode.EXIT_EXPECTED_ERROR
        }

        val listing = Listing(sender, stack.type, worthTable.getWorth(stack), stack.amount)
        vars.putAll(Variables().withListing(listing, sender.inventory).get())

        sender.sendMessage(locale.translate(Constants.LanguagePaths.WORTH_MESSAGE, vars))
        ExitCode.EXIT_SUCCESS
    }
