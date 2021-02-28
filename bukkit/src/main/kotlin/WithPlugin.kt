/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands

interface WithPlugin {
    val plugin
        get() = StickyCommands.plugin

    val logger
        get() = this.plugin.logger

    val config
        get() = this.plugin.config
}
