/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.util

object Constants {
    private const val prefix = "stickycommands"
    const val DEFAULT_WALKING_SPEED = 0.2f // 0.1 is sneak, supposedly.
    const val DEFAULT_FLYING_SPEED = 0.1f // according to google

    object Permissions {
        const val AFK = "$prefix.afk"

        const val POWERTOOL = "$prefix.powertool"
        const val POWERTOOL_CLEAR = "$POWERTOOL.clear"
        const val POWERTOOL_TOGGLE = "$POWERTOOL.toggle"
        const val POWERTOOL_VIEW_ALL_COMMANDS = "$POWERTOOL.viewallcommands"

        const val SELL = "$prefix.sell"
        const val SELL_HAND = "$SELL.hand"
        const val SELL_INVENTORY = "$SELL.inventory"
        const val SELL_LOG = "$SELL.log"

        const val WORTH = "$prefix.worth"

        const val SPEED = "$prefix.speed"

        const val SEEN = "$prefix.seen"
        const val WHOIS = "$prefix.whois"
        const val WHOIS_IP = "$WHOIS.ip"
    }

    object LanguagePaths {
        const val PREFIX = "prefix"
        const val NO_PERMISSION = "no-permission"
        const val MUST_BE_PLAYER = "must-be-player"
        const val SERVER_ERROR = "server-error"
        const val INVALID_SYNTAX = "invalid-syntax"
        const val PLAYER_DOES_NOT_EXIST = "player-does-not-exist"
        const val PLAYER_HAS_NOT_JOINED = "player-has-not-joined"
        const val NOT_ONLINE_PLAYER = "not-online-player"
        const val INVALID_GROUP = "invalid-group"

        const val SEEN_MESSAGE = "seen-message"
        const val WHOIS_MESSAGE = "whois-message"
        const val TOP_MESSAGE = "top-message"
        const val JUMP_MESSAGE = "jump-message"
        const val SPEED_MESSAGE = "speed-message"

        const val RELOAD_CONFIG_SUCCESS = "reload.configs-success"
        const val RELOAD_DATABASE_SUCCESS = "reload.database-success"
        const val RELOAD_ERROR = "reload.error"

        const val CANNOT_SELL = "sell.cannot-sell"
        const val SELL_BAD_WORTH = "sell.bad-worth"
        const val WORTH_MESSAGE = "sell.worth-message"
        const val SELL_MESSAGE = "sell.sell-message"
        const val SELL_MUST_CONFIRM = "sell.must-confirm"
        const val SELL_LOG_MESSAGE = "sell.log.log-message"
        const val SELL_LOG_LOG = "sell.log.log"
        const val SELL_LOG_LOG_HOVER = "sell.log.log-hover"
        const val SELL_LOG_NO_SALES = "sell.log.no-sales"
        const val SELL_LOG_PAGINATOR = "sell.log.paginator"

        const val AFK_MESSAGE = "afk.afk"
        const val NOT_AFK = "afk.not-afk"
        const val AFK_KICK = "afk.afk-kick"

        const val NO_HAT = "hat.no-hat"
        const val NEW_HAT = "hat.new-hat"
        const val REMOVE_HAT = "hat.remove-hat"

        const val ITEM_MESSAGE = "item-message"
        const val MEMORY_MESSAGE = "memory-message"

        const val YOU_WERE_KILLED = "kill.you-were-killed"
        const val YOU_KILLED = "kill.you-killed"
        const val SUICIDE = "kill.suicide"

        const val SMITE_OTHER_PLAYER = "smite.smite-other-player"
        const val SMITE_BLOCK = "smite.smite-block"
        const val SMITE_MESSAGE = "smite.smite-message"
        const val SMITE_IMMUNE = "smite.smite-immune"
        const val SMITE_YOURSELF = "smite.yourself"

        const val YOU_WHIPPED = "whip.you-whipped"
        const val YOU_WERE_WHIPPED = "whip.where-whipped"

        const val POWERTOOL_CLEARED = "powertool.cleared"
        const val POWERTOOL_ASSIGNED = "powertool.assigned"
        const val POWERTOOL_CANNOT_BIND_AIR = "powertool.cannot-bind-air"
        const val POWERTOOL_TOGGLED = "powertool.toggled"
        const val NO_POWERTOOL = "powertool.no-powertool"

        const val PLAYER_TIME_RESET = "player-time.time-reset"
        const val PLAYER_TIME_SET = "player-time.time-set"
    }

    object Descriptions {
        const val AFK = "Let the server know you're afk!"

        const val SELL = "Sell an item"
        const val SELL_INVENTORY = "Sell all of an item from your inventory"
        const val SELL_LOG = "Check the logs of recent sales"

        const val WORTH = "Check the worth of an item."

        const val POWERTOOL = "Bind an item to a command"
        const val POWERTOOL_CLEAR = "Clear your item of a command"
        const val POWERTOOL_TOGGLE = "Toggle your powertool"

        const val SPEED = "Change your fly or walk speed"

        const val SEEN = "Check when a player was last online"

        const val WHOIS = "Lookup a player"
    }

    object SettingsPaths {
        const val TRANSLATION_FILE = "translation-file"
        const val WORTH_FILE = "worth-file"
        const val ALLOW_SELLING = "allow-selling"
        const val AUTO_SELL = "auto-sell"
        const val AFK_TIMEOUT = "afk-timeout"
        const val DEBUG = "debug"
        const val SERVER = "server"

        const val DATABASE_HOST = "database.host"
        const val DATABASE_PORT = "database.port"
        const val DATABASE_DATABASE = "database.database"
        const val DATABASE_USERNAME = "database.username"
        const val DATABASE_PASSWORD = "database.password"
        const val DATABASE_TABLE_PREFIX = "database.table-prefix"
        const val DATABASE_MAX_RECONNECTS = "database.max-reconnects"
        const val DATABASE_USE_SSL = "database.use-ssl"
    }
}
