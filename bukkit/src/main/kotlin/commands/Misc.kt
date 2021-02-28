/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.commands

import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.util.Variables
import dev.jorel.commandapi.CommandAPICommand
import org.bukkit.entity.Player

internal val locale = StickyCommands.localeProvider!!
internal val worthTable = StickyCommands.plugin.worthTable
internal val market = StickyCommands.plugin.market
internal val postgresHandler = StickyCommands.plugin.postgresHandler
internal val plugin = StickyCommands.plugin

internal fun playerVariables(player: Player, target: Boolean) = Variables().withPlayer(player, target).get()
internal fun playerVariables(player: Player) = playerVariables(player, false)

internal fun commandStub(name: String, permission: String): CommandAPICommand = CommandAPICommand(name).withPermission(permission)
