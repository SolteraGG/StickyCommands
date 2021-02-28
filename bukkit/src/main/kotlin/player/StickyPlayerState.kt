/*
 * Copyright (c) 2021 DumbDogDiner <dumbdogdiner.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package com.dumbdogdiner.stickycommands.player

import com.dumbdogdiner.stickyapi.bukkit.util.ServerUtil
import com.dumbdogdiner.stickycommands.StickyCommands
import com.dumbdogdiner.stickycommands.WithPlugin
import com.dumbdogdiner.stickycommands.api.player.PlayerState
import com.dumbdogdiner.stickycommands.api.player.SpeedType
import com.dumbdogdiner.stickycommands.database.tables.Users
import com.dumbdogdiner.stickycommands.util.Constants
import com.dumbdogdiner.stickycommands.util.Variables
import me.xtomyserrax.StaffFacilities.SFAPI
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class StickyPlayerState(
    private val player: Player
) : PlayerState, WithPlugin {

    private var _afk: Boolean = false
    private var _afkTime: Int = 0

    override fun getPlayer(): Player {
        return this.player
    }

    override fun isAfk(): Boolean {
        return this._afk
    }

    override fun getAfkTime(): Int {
        return this._afkTime
    }

    override fun incrementAfkTime() {
        this._afkTime++
    }

    override fun resetAfkTime() {
        this._afkTime = 0
    }

    override fun isHidden(): Boolean {
        if (StickyCommands.staffFacilitiesEnabled) {
            val player = getPlayer()
        return SFAPI.isPlayerFakeleaved(player) ||
                    SFAPI.isPlayerStaffVanished(player) ||
                    SFAPI.isPlayerVanished(player) ||
                    isVanished
        }
        return false
    }

    /**
     * Checks if a given player is in a vanished state.
     *
     * @return Whether the user is vanished.
     */
    override fun isVanished(): Boolean {
        for (meta in getPlayer().getMetadata("vanished")) {
            if (meta.asBoolean()) {
                return true
            }
        }
        return false
    }

    override fun setAfk(isAfk: Boolean) {
        setAfk(isAfk, false)
    }

    override fun setAfk(isAfk: Boolean, broadcast: Boolean) {
        this._afk = isAfk
        // reset the time if we're unsetting their afk status
        if (!isAfk) {
            this._afkTime = 0
            this.player.removeMetadata("stickycommands_afk", this.plugin)
        } else {
            this.player.setMetadata("stickycommands_afk", FixedMetadataValue(this.plugin, "stickycommands_afk"))
        }

        if (broadcast) {
            if (!isHidden) {
                val node = if (isAfk) Constants.LanguagePaths.AFK_MESSAGE else Constants.LanguagePaths.NOT_AFK
                // TODO: Make method for getting all variables related to a user, such as location, username, uuid, etc
                val vars = Variables().withPlayer(player, false).get()

                ServerUtil.broadcastMessage(StickyCommands.localeProvider!!.translate(node, vars))
            }
        }
    }

    override fun hasFlyModeEnabled(): Boolean {
        return this.player.allowFlight
    }

    override fun getSpeed(type: SpeedType): Float {
        return when (type) {
            SpeedType.WALK -> this.player.walkSpeed
            SpeedType.FLY -> this.player.flySpeed
        }
    }

    override fun setSpeed(speed: Float) {
        if (this.player.isFlying)
            setSpeed(SpeedType.FLY, speed)
        else
            setSpeed(SpeedType.WALK, speed)
    }

    override fun setSpeed(type: SpeedType, speed: Float) {
        // we can't reassign params, so we have to do this.
        var _speed = speed

        // sanity checks to make sure bukkit doesn't complain
        // and that the speed is correct for walking
        if (_speed <= 0f) _speed = 0.1f else if (speed > 1f) _speed = 1f

        if (type === SpeedType.WALK) _speed = if (_speed + 0.1f > 1f) _speed else _speed + 0.1f

        when (type) {
            SpeedType.FLY -> {
                this.getPlayer().flySpeed = _speed
                transaction(this.plugin.postgresHandler.db) {
                    Users.update({ Users.uniqueId eq player.uniqueId.toString() }) {
                        it[flySpeed] = _speed
                    }
                    commit()
                }
            }
            SpeedType.WALK -> {
                this.getPlayer().walkSpeed = _speed
                transaction(this.plugin.postgresHandler.db) {
                    Users.update({ Users.uniqueId eq player.uniqueId.toString() }) {
                        it[walkSpeed] = _speed
                    }
                    commit()
                }
            }
        }
    }

    override fun setFlyModeEnabled(flyEnabled: Boolean) {
        this.player.allowFlight = flyEnabled
    }
}
