/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.database.tables

import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.util.Constants
import org.jetbrains.exposed.sql.Table

object Listings : Table(StickyCommands.plugin.config.getString(Constants.SettingsPaths.DATABASE_TABLE_PREFIX) + "listings") {
    val id = integer("id").autoIncrement()

    var listedAt = long("listed_at").clientDefault { System.currentTimeMillis() }

    var seller = varchar("seller", 36)

    var item = varchar("item", 256)

    var quantity = integer("quantity")

    var value = double("value")

    var buyer = varchar("buyer", 36).nullable()

    var sold = bool("sold").default(false)

    override val primaryKey = PrimaryKey(id)
}
