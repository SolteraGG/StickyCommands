/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.database.tables

import org.jetbrains.exposed.sql.Table

object Listings : Table(prefix + "listings"){
    val id = integer("id").autoIncrement()

    var listedAt = long("listed_at").clientDefault { System.currentTimeMillis() }

    var seller = varchar("seller", 36)

    var item = varchar("item", 256)

    var quantity = integer("quantity")

    var value = double("value")

    // TODO: change this to a UUID object directly if we can??
    var buyer = varchar("buyer", 36).nullable()

    var sold = bool("sold").default(false)

    override val primaryKey = PrimaryKey(id)
}
