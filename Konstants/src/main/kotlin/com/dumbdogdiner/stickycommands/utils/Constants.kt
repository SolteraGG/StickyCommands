package com.dumbdogdiner.stickycommands.utils

/**
 * Class for storing constant values such as configuration paths
 */
object Constants {
    private const val prefix = "stickycommands"
    const val DEFAULT_WALKING_SPEED = 0.2f // 0.1 is sneak, supposedly.
    const val DEFAULT_FLYING_SPEED = 0.1f // according to google
    const val SMITE_EXPLOSION_STRENGTH = 1.5f
    const val SMITE_TARGET_RANGE = 512


    object Commands {
        const val RULEBOOK = "rulebook"
        const val RULES = "rules"
        const val AFK = "afk"
        const val WORTH = "worth"
        const val SELL = "sell"
        const val SPEED = "speed"

    }

    object Permissions {
        const val AFK = "$prefix.afk"
        const val SBACK = "$prefix.sback"
        const val SBACK_OTHER = "$prefix.sback.other"

        const val HAT = "$prefix.hat"

        const val KILL = "$prefix.kill"
        const val KILL_IMMUNE = "$KILL.immune"
        const val KILL_OTHERS = "$KILL.others"
        const val KILL_ENTITIES = "$KILL.ENTITIES"

        const val POWERTOOL = "$prefix.powertool"
        const val POWERTOOL_CLEAR = "$POWERTOOL.clear"
        const val POWERTOOL_TOGGLE = "$POWERTOOL.toggle"
        const val POWERTOOL_VIEW_ALL_COMMANDS = "$POWERTOOL.viewallcommands"

        const val SEEN = "$prefix.seen"

        const val SELL = "$prefix.sell"
        const val SELL_HAND = "$SELL.hand"
        const val SELL_INVENTORY = "$SELL.inventory"
        const val SELL_LOG = "$SELL.log"

        const val SMITE = "$prefix.smite"
        const val SMITE_IMMUNE = "$prefix.immune"

        const val SPEED = "$prefix.speed"

        const val WHOIS = "$prefix.whois"
        const val WHOIS_IP = "$WHOIS.ip"

        const val WORTH = "$prefix.worth"
    }

    object Files {
        const val MEDALLION_UUIDS = "uuids_by_season.csv"
        const val ITEM_WORTHS = "worth.yml"
    }

    // Todo: auto-generate this stuff in the future
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

        const val KILL_IMMUNE = "kill.kill-immune"
        const val YOU_WERE_KILLED = "kill.you-were-killed"
        const val YOU_KILLED = "kill.you-killed"
        const val YOU_KILLED_ENTITIES = "kill.you-killed-entities"
        const val SUICIDE = "kill.suicide"

        const val BACK_NO_PREVIOUS = "back.no-previous"
        const val BACK_TELEPORTED = "back.teleported"
        const val BACK_TELEPORTED_OTHER = "back.teleported-other"

        private const val smite_prefix = "smite"
        const val SMITE_ENTITIES = "$smite_prefix.smite-entities"
        const val SMITE_OTHER = "$smite_prefix.smite-other-player"
        const val SMITE_BLOCK = "$smite_prefix.smite-block"
        const val SMITE_MESSAGE = "$smite_prefix.smite-message"
        const val SMITE_IMMUNE = "$smite_prefix.smite-immune"
        const val SMITE_YOURSELF = "$smite_prefix.yourself"

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

    // TODO: auto-generate this stuff in the future, owo
    object SettingsPaths {
        const val TRANSLATION_FILE = "translation-file"
        const val ALLOW_SELLING = "allow-selling"
        const val AUTO_SELL = "auto-sell"
        const val AFK_TIMEOUT = "afk-timeout"
        const val DEBUG = "debug"
        const val SERVER = "server"
        const val ENABLE_MEDALLIONS = "enable-medallions"
    }
    object DatabaseConstants {
        const val DATABASE_HOST = "database.host"
        const val DATABASE_PORT = "database.port"
        const val DATABASE_DATABASE = "database.database"
        const val DATABASE_USERNAME = "database.username"
        const val DATABASE_PASSWORD = "database.password"
        const val DATABASE_TABLE_PREFIX = "database.table-prefix"
        const val DATABASE_MAX_RECONNECTS = "database.max-reconnects"
        const val DATABASE_USE_SSL = "database.use-ssl"
    }

    object Messages {

    }
}