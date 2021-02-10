/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickyapi.bukkit.command.BukkitCommandBuilder
import com.dumbdogdiner.stickyapi.common.arguments.Arguments
import com.dumbdogdiner.stickyapi.common.command.ExitCode
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.api.economy.Listing
import com.dumbdogdiner.stickycommands.util.InventoryUtil
import com.dumbdogdiner.stickycommands.util.Variables
import com.google.common.collect.ImmutableList
import java.util.HashMap
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SellCommand {
    private val locale = StickyCommands.localeProvider!!
    private val worthTable = StickyCommands.plugin.worthTable

    val command = BukkitCommandBuilder("sell")
        .description("Sell an item")
        .permission("stickycommands.sell")
        .requiresPlayer()
        .playSound()

        .onExecute { sender, args, vars ->
            if (!execute(sender, args, vars, false))
                return@onExecute ExitCode.EXIT_EXPECTED_ERROR
            return@onExecute ExitCode.EXIT_SUCCESS
        }
        .subCommand(
            BukkitCommandBuilder("inventory")
                .permission("stickycommands.sell.inventory")
                .description("Sell all of an item from your inventory")
                .requiresPlayer()
                .playSound()
                .onExecute { sender, args, vars ->
                    if (!execute(sender, args, vars, true))
                        return@onExecute ExitCode.EXIT_EXPECTED_ERROR
                    return@onExecute ExitCode.EXIT_SUCCESS
                }
                .onError { exitCode, _, _, vars ->
                    when (exitCode) {
                        ExitCode.EXIT_PERMISSION_DENIED -> locale.translate("no-permission", vars)
                        ExitCode.EXIT_MUST_BE_PLAYER -> locale.translate("must-be-player", vars)
                        ExitCode.EXIT_ERROR -> locale.translate("server-error", vars)
                        ExitCode.EXIT_INVALID_SYNTAX -> locale.translate("invalid-syntax", vars)
                    }
                }
        )

        .onError { exitCode, _, _, vars ->
            when (exitCode) {
                ExitCode.EXIT_PERMISSION_DENIED -> locale.translate("no-permission", vars)
                ExitCode.EXIT_MUST_BE_PLAYER -> locale.translate("must-be-player", vars)
                ExitCode.EXIT_ERROR -> locale.translate("server-error", vars)
                ExitCode.EXIT_INVALID_SYNTAX -> locale.translate("invalid-syntax", vars)
            }
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
        sender.sendMessage(locale.translate("sell.sell-message", vars))
        listing.list()

        return true
    }

    private fun canSell(stack: ItemStack): Boolean {

        if (stack.type == Material.AIR || worthTable.isSellable(stack) && worthTable.getWorth(stack) <= 0.0) {
            return false
        }
        return true
    }

    private fun doChecks(sender: CommandSender, stack: ItemStack, args: Arguments, vars: HashMap<String, String>): Boolean {
        if (!canSell(stack)) {
            sender.sendMessage(locale.translate("sell.cannot-sell", vars))
            return false
        }

        if (!args.exists("confirm")) {
            sender.sendMessage(locale.translate("sell.must-confirm", vars))
            return false
        }

        return true
    }
}
