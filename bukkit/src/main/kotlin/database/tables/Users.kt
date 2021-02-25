/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.database.tables

import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.WithPlugin
import com.dumbdogdiner.stickycommands.util.Constants
import org.jetbrains.exposed.sql.Table

object Users : Table(StickyCommands.plugin.config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX) + "users"),
    WithPlugin {

    val uniqueId = varchar("unique_id", 36)

    val name = varchar("name", 16)

    val ipAddress = varchar("ip_address", 45)

    val firstSeen = long("first_seen").clientDefault { System.currentTimeMillis() }

    val lastSeen = long("last_seen")

    val lastWorld = text("last_world").default(config.getString("default_sback_world")!!)

    val walkSpeed = float("walk_speed").default(0.2F)

    val flySpeed = float("fly_speed").default(0.1F)

    val isOnline = bool("online").default(false)

    val location = text("location")

    override val primaryKey = PrimaryKey(uniqueId)
}
