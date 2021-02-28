/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.database.tables

import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.util.Constants
import org.jetbrains.exposed.sql.Table

private val server = StickyCommands.plugin.config.getString(Constants.SettingsPaths.SERVER, "unknown")
private val prefix = StickyCommands.plugin.config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX, "stickycommands_")

object Locations : Table("$prefix${server}_locations") {

    val uniqueId = varchar("unique_id", 36)

    val world = varchar("world", 36)

    val x = double("x")

    val y = double("y")

    val z = double("z")

    val yaw = float("yaw")

    val pitch = float("pitch")

    override val primaryKey = PrimaryKey(uniqueId)
}
