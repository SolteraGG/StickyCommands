/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.common.arguments.Arguments
import com.dumbdogdiner.stickyapi.common.chat.ChatMessage
import com.dumbdogdiner.stickyapi.common.command.ExitCode
import com.dumbdogdiner.stickyapi.common.util.NumberUtil
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.api.economy.Listing
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.InventoryUtil
import com.dumbdogdiner.stickycommands.util.Variables
import java.util.HashMap
import kotlin.math.roundToInt
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

val sellCommand = commandStub("sell", Constants.Descriptions.SELL, Constants.Permissions.SELL)
            .requiresPlayer()
            .onExecute { sender, args, vars ->
                if (!execute(sender, args, vars, false))
                    return@onExecute ExitCode.EXIT_EXPECTED_ERROR
                return@onExecute ExitCode.EXIT_SUCCESS
    }
    .subCommand(
        commandStub("log", Constants.Descriptions.SELL_INVENTORY, Constants.Permissions.SELL_INVENTORY)
            .requiresPlayer()
            .onExecute { sender, args, vars ->
                if (!execute(sender, args, vars, true))
                    return@onExecute ExitCode.EXIT_EXPECTED_ERROR
                return@onExecute ExitCode.EXIT_SUCCESS
            }

    )

    // TODO Clean this sh*t up, omg -zach
    .subCommand(
        commandStub("log", Constants.Descriptions.SELL_LOG, Constants.Permissions.SELL_LOG)
            .requiresPlayer()
            .onExecute { sender, args, vars ->
                args.optionalString("page") // so F*CKING DUMB but optionalInt is broken and we have to do this.
                val page = if (!args.exists("page") || !NumberUtil.isNumeric(args.getString("page"))) 1
                else args.getString("page").toInt() // The arguments class is honestly dumb and we need to rewrite it, maybe validators?

                val listings = market.getListings(Listing.SortBy.DATE_ASCENDING, page, 8)
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
                    return@onExecute ExitCode.EXIT_SUCCESS
                }

                vars["current"] = page.toString()
                vars["total"] = pages.toInt().toString()
                sender.sendMessage(locale.translate(Constants.LanguagePaths.SELL_LOG_PAGINATOR, vars))
                return@onExecute ExitCode.EXIT_SUCCESS
            }
    )

    .onTabComplete { _, _, args ->
        if (args.rawArgs.size > 0) {
            return@onTabComplete when (args.rawArgs[0]) {
                "inventory" -> listOf("confirm")
                "hand" -> listOf("confirm")
                "log" -> listOf((1..(market.listingCount / 8)).toString())
                else -> listOf()
            }
        }

        return@onTabComplete listOf("hand", "inventory", "confirm")
    }

private fun execute(sender: CommandSender, args: Arguments, vars: HashMap<String, String>, inventory: Boolean): Boolean {
    args.optionalFlag("confirm", "confirm")
    val player = sender as Player
    vars.putAll(Variables().withPlayer(player, false).get())

    val stack = player.inventory.itemInMainHand
    if (!doChecks(sender, stack, args, vars))
        return false

    val listing = Listing(player, stack.type, worthTable.getWorth(stack), if (inventory) InventoryUtil.count(player.inventory, stack.type) else stack.amount)
    vars.putAll(Variables().withListing(listing, player.inventory).get())

    if (StickyCommands.plugin.config.getBoolean("auto-sell", true) && listing.seller.isOnline) {
        InventoryUtil.removeItems(player.inventory, listing.material, listing.quantity)
        StickyCommands.economy!!.depositPlayer(player, listing.price)
    }
    sender.sendMessage(locale.translate(Constants.LanguagePaths.SELL_MESSAGE, vars))
    listing.list()

    return true
}

private fun doChecks(sender: CommandSender, stack: ItemStack, args: Arguments, vars: HashMap<String, String>): Boolean {
    if (!worthTable.isSellable(stack)) {
        sender.sendMessage(locale.translate(Constants.LanguagePaths.CANNOT_SELL, vars))
        return false
    }

    if (!args.exists("confirm")) {
        sender.sendMessage(locale.translate(Constants.LanguagePaths.SELL_MUST_CONFIRM, vars))
        return false
    }

    return true
}
