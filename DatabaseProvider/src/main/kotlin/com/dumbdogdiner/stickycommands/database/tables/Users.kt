/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.database.tables

import org.jetbrains.exposed.sql.Table
import com.dumbdogdiner.stickycommands.database.tables.TableVars.prefix
import com.dumbdogdiner.stickycommands.database.tables.TableVars.server

object Users : Table(prefix + "users") {
    @JvmStatic
    val uniqueId = varchar("unique_id", 36)

    @JvmStatic
    val name = varchar("name", 16)

    @JvmStatic
    val ipAddress = varchar("ip_address", 45)

    @JvmStatic
    val firstSeen = long("first_seen").clientDefault { System.currentTimeMillis() }

    @JvmStatic
    val lastSeen = long("last_seen")

    @JvmStatic
    val lastServer = text("last_server").default(server)

    @JvmStatic
    val walkSpeed = float("walk_speed").default(0.2F)

    @JvmStatic
    val flySpeed = float("fly_speed").default(0.1F)

    @JvmStatic
    val isOnline = bool("online").default(false)

    @JvmStatic
    val firstJoinItemsGiven = bool("first_join_items_given").default(false)

    override val primaryKey = PrimaryKey(uniqueId)
}
