/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil
import com.dumbdogdiner.stickyapi.common.chat.ChatMessage
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.api.economy.Listing
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.InventoryUtil
import com.dumbdogdiner.stickycommands.util.Variables
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import kotlin.math.roundToInt
import org.bukkit.Bukkit
import org.bukkit.entity.Player

val sellCommand = commandStub("sell", Constants.Permissions.SELL)
    .executesPlayer(PlayerCommandExecutor { sender, args ->
        sender.sendMessage(locale.translate(Constants.LanguagePaths.SELL_MUST_CONFIRM, Variables().withPlayer(sender, false).get()))
    })
    .withSubcommand(
        commandStub("confirm", Constants.Permissions.SELL_INVENTORY)
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                execute(sender, false)
            })
    )
    .withSubcommand(
        commandStub("inventory", Constants.Permissions.SELL_INVENTORY)
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                execute(sender, true)
            })
    )
    .withSubcommand(
        commandStub("log", Constants.Permissions.SELL_LOG)
        .executesPlayer(PlayerCommandExecutor { sender, args ->
            executeLog(sender, 1, null)
        })
    )
    .withSubcommand(
        commandStub("log", Constants.Permissions.SELL_LOG)
            .withArguments(IntegerArgument("page"))
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                executeLog(sender, args[0] as Int, null)
            })
    )
    .withSubcommand(
        commandStub("log", Constants.Permissions.SELL_LOG)
            .withArguments(StringArgument("player"))
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                executeLog(sender, 1, Bukkit.getPlayer(args[0].toString()))
            })
    ).withSubcommand(
        commandStub("log", Constants.Permissions.SELL_LOG)
            .withArguments(StringArgument("player"))
            .withArguments(IntegerArgument("page"))
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                executeLog(sender, args[1] as Int, Bukkit.getPlayer(args[0].toString()))
            })
    )

private fun execute(sender: Player, inventory: Boolean): Boolean {
    val vars = Variables().withPlayer(sender, false).get()

    val stack = sender.inventory.itemInMainHand
    if (!worthTable.isSellable(stack)) {
        sender.sendMessage(locale.translate(Constants.LanguagePaths.CANNOT_SELL, vars))
        SoundUtil.sendError(sender)
        return false
    }

    val listing = Listing(sender, stack.type, worthTable.getWorth(stack), if (inventory) InventoryUtil.count(sender.inventory, stack.type) else stack.amount)
    vars.putAll(Variables().withListing(listing, sender.inventory).get())

    if (StickyCommands.plugin.config.getBoolean("auto-sell", true) && listing.seller.isOnline) {
        InventoryUtil.removeItems(sender.inventory, listing.material, listing.quantity)
        StickyCommands.economy!!.depositPlayer(sender, listing.price)
    }
    sender.sendMessage(locale.translate(Constants.LanguagePaths.SELL_MESSAGE, vars))
    listing.list()
    SoundUtil.sendSuccess(sender)
    return true
}

// TODO maybe make this look a little more nice
private fun executeLog(sender: Player, page: Int, player: Player?): Boolean {
    val vars = Variables().withPlayer(sender, false).get()

    val listings = if (player == null) market.getListings(Listing.SortBy.DATE_DESCENDING, page, 8) else market.getListingsOfPlayer(player, Listing.SortBy.DATE_DESCENDING, page, 8)
    sender.sendMessage(locale.translate(Constants.LanguagePaths.SELL_LOG_MESSAGE, vars))

    var listingCount = 0
    listings.forEach {
        vars.putAll(Variables().withListing(it).get())
        sender.spigot().sendMessage(ChatMessage(locale.translate(Constants.LanguagePaths.SELL_LOG_LOG, vars)).setHoverMessage(
            locale.translate(Constants.LanguagePaths.SELL_LOG_LOG_HOVER, vars)).component)
        ++listingCount
    }

    val tmpPages = market.listingCount.toDouble() / 8
    val pages = if (tmpPages > tmpPages.roundToInt()) Math.round(tmpPages + 1).toDouble() else tmpPages // ew but it works??

    if (listingCount < 1 || page > pages) {
        sender.sendMessage(locale.translate(Constants.LanguagePaths.SELL_LOG_NO_SALES, vars))
        SoundUtil.sendSuccess(sender)
        return true
    }

    vars["current"] = page.toString()
    vars["total"] = pages.toInt().toString()
    sender.sendMessage(locale.translate(Constants.LanguagePaths.SELL_LOG_PAGINATOR, vars))
    SoundUtil.sendSuccess(sender)
    return true
}
