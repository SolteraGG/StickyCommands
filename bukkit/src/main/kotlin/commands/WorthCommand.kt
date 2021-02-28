/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil
import com.dumbdogdiner.stickycommands.api.economy.Listing
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import dev.jorel.commandapi.executors.PlayerCommandExecutor

val worthCommand = commandStub("sworth", Constants.Permissions.WORTH)
    .executesPlayer(PlayerCommandExecutor { sender, _ ->
        val vars = Variables().withPlayer(sender, false).get()
        val stack = sender.inventory.itemInMainHand
        if (!worthTable.isSellable(stack)) {
            sender.sendMessage(locale.translate(Constants.LanguagePaths.CANNOT_SELL, vars))
            SoundUtil.sendError(sender)
            return@PlayerCommandExecutor
        }

        val listing = Listing(sender, stack.type, worthTable.getWorth(stack), stack.amount)
        vars.putAll(Variables().withListing(listing, sender.inventory).get())

        sender.sendMessage(locale.translate(Constants.LanguagePaths.WORTH_MESSAGE, vars))
        SoundUtil.sendSuccess(sender)
    })
