/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.database.tables

import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.util.WithPlugin
import org.jetbrains.exposed.sql.Table

object Users : Table(StickyCommands.plugin.config.getString("database.table-prefix") + "users"), WithPlugin {

    val uniqueId = varchar("unique_id", 36)

    val ipAddress = varchar("ip_address", 45)

    val firstSeen = long("first_seen").clientDefault { System.currentTimeMillis() / 1000L }

    val lastSeen = long("last_seen")

    val lastServer = text("last_server").default(config.getString("server")!!)

    val walkSpeed = float("walk_speed").default(0.2F)

    val flySpeed = float("fly_speed").default(0.1F)

    val isOnline = bool("online").default(false)

    override val primaryKey = PrimaryKey(uniqueId)
}
