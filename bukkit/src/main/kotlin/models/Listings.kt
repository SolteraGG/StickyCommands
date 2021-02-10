/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.models

import org.jetbrains.exposed.sql.Table

object Listings : Table() {
    val id = integer("id").autoIncrement()

    var date = long("time_listed").clientDefault { System.currentTimeMillis() / 1000L }

    var seller = varchar("seller", 36)

    var item = varchar("item", 256)

    var quantity = integer("quantity")

    var value = double("value")

    var buyer = varchar("buyer", 36).nullable()

    override val primaryKey = PrimaryKey(id)
}
