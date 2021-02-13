/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.command.BukkitCommandBuilder
import com.dumbdogdiner.stickyapi.common.arguments.Arguments
import com.dumbdogdiner.stickyapi.common.chat.ChatMessage
import com.dumbdogdiner.stickyapi.common.command.ExitCode
import com.dumbdogdiner.stickyapi.common.util.StringUtil
import com.dumbdogdiner.stickyapi.common.util.TimeUtil
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.api.economy.Listing
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.InventoryUtil
import com.dumbdogdiner.stickycommands.util.Variables
import com.google.common.collect.ImmutableList
import java.util.HashMap
import kotlin.math.roundToInt
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SellCommand {
    private val locale = StickyCommands.localeProvider!!
    private val worthTable = StickyCommands.plugin.worthTable

    val command = BukkitCommandBuilder("sell")
        .permission(Constants.Permissions.SELL)
        .description(Constants.Descriptions.SELL)
        .requiresPlayer()
        .playSound()

        .onExecute { sender, args, vars ->
            if (!execute(sender, args, vars, false))
                return@onExecute ExitCode.EXIT_EXPECTED_ERROR
            return@onExecute ExitCode.EXIT_SUCCESS
        }

        .subCommand(
            BukkitCommandBuilder("inventory")
                .permission(Constants.Permissions.SELL_INVENTORY)
                .description(Constants.Descriptions.SELL_INVENTORY)
                .requiresPlayer()
                .playSound()

                .onExecute { sender, args, vars ->
                    if (!execute(sender, args, vars, true))
                        return@onExecute ExitCode.EXIT_EXPECTED_ERROR
                    return@onExecute ExitCode.EXIT_SUCCESS
                }
                .onError { exitCode, sender, _, vars ->
                    onError(sender, exitCode, vars)
                }

        )

            // TODO Clean this sh*t up, omg -zach
        .subCommand(
            BukkitCommandBuilder("log")
                .permission(Constants.Permissions.SELL_LOG)
                .description(Constants.Descriptions.SELL_LOG)
                .requiresPlayer()
                .playSound()
                .onExecute { sender, args, vars ->

                    args.optionalInt("page")

                    val market = StickyCommands.plugin.market
                    val page = args.getInt("page")
                    val listings = market.getListings(Listing.SortBy.DATE_ASCENDING, page ?: 1, 8)

                    sender.sendMessage(locale.translate(Constants.LanguagePaths.SELL_LOG_MESSAGE, vars))
                    var i = 0
                    for (listing in listings) {
                        ++i
                        vars["log_player"] = listing.seller.name
                        vars["saleid"] = listing.id.toString()
                        vars["item"] = StringUtil.capitaliseSentence(listing.material.toString().replace("_", " "))
                        vars["item_enum"] = listing.material.toString()
                        vars["amount"] = listing.quantity.toString()
                        vars["price"] = (listing.price).toString()
                        vars["balance_change"] = listing.price.toString()
                        vars["short_date"] = TimeUtil.significantDurationString(System.currentTimeMillis() - listing.listedAt.time) // dumb but whatever
                        vars["date_duration"] = TimeUtil.expirationTime(System.currentTimeMillis() - listing.listedAt.time)
                        sender.spigot().sendMessage(
                            ChatMessage(
                                locale.translate(Constants.LanguagePaths.SELL_LOG_LOG, vars
                                )
                            ).setHoverMessage(locale.translate(Constants.LanguagePaths.SELL_LOG_LOG_HOVER, vars)).component
                        )
                    }

                    val tmpPages = market.listingCount.toDouble() / 8
                    val pages = if (tmpPages > tmpPages.roundToInt()) Math.round(tmpPages + 1).toDouble() else tmpPages // ew but it works??

                    if (i < 1 || page > pages) {
                        sender.sendMessage(locale.translate(Constants.LanguagePaths.SELL_LOG_NO_SALES, vars))
                        return@onExecute ExitCode.EXIT_SUCCESS
                    }
                    vars["current"] = page.toInt().toString()
                    vars["total"] = pages.toInt().toString()
                    sender.sendMessage(locale.translate(Constants.LanguagePaths.SELL_LOG_PAGINATOR, vars))
                    return@onExecute ExitCode.EXIT_SUCCESS
                }
        )
        .onError { exitCode, sender, _, vars ->
            onError(sender, exitCode, vars)
        }

        .onTabComplete { _, _, _ ->
            ImmutableList.of("hand", "inventory", "log", "confirm")
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

    private fun onError(sender: CommandSender, exitCode: ExitCode, vars: HashMap<String, String>) {
        when (exitCode) {
            ExitCode.EXIT_PERMISSION_DENIED -> sender.sendMessage(locale.translate(Constants.LanguagePaths.NO_PERMISSION, vars))
            ExitCode.EXIT_MUST_BE_PLAYER -> sender.sendMessage(locale.translate(Constants.LanguagePaths.MUST_BE_PLAYER, vars))
            ExitCode.EXIT_ERROR -> sender.sendMessage(locale.translate(Constants.LanguagePaths.SERVER_ERROR, vars))
            else -> ""
        }
    }
}
