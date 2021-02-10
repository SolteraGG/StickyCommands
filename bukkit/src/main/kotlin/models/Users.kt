/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.models

import com.dumbdogdiner.stickycommands.util.WithPlugin
import org.jetbrains.exposed.sql.Table

object Users : Table(), WithPlugin {
    val id = integer("id").autoIncrement()

    var player = varchar("player", 36)

    var ipAddress = varchar("ip_address", 45)

    var firstSeen = long("first_seen").clientDefault { System.currentTimeMillis() / 1000L }

    var lastSeen = long("last_seen")

    var lastServer = text("last_server").default(config.getString("server")!!)

    var walkSpeed = float("walk_speed").default(0.2F)

    var flySpeed = float("fly_speed").default(0.1F)

    var isOnline = bool("online").default(false)

    override val primaryKey = PrimaryKey(id)
}
