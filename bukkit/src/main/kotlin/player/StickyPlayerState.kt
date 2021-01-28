package com.dumbdogdiner.stickycommands.player

import com.dumbdogdiner.stickycommands.api.player.PlayerState
import com.dumbdogdiner.stickycommands.api.player.SpeedType
import org.bukkit.entity.Player

class StickyPlayerState(
    private val player: Player
    ): PlayerState {

    private var _afk: Boolean = false

    override fun getPlayer(): Player {
        return this.player
    }

    override fun isAfk(): Boolean {
        return this._afk
    }

    override fun setAfk(isAfk: Boolean) {
        this._afk = isAfk
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

    override fun setSpeed(type: SpeedType, speed: Float) {
        // we can't reassign vars, so we have to do this.
        var _speed = speed

        // sanity checks to make sure bukkit doesn't complain
        // and that the speed is correct for walking
        if (_speed <= 0f) _speed = 0.1f else if (speed > 1f) _speed = 1f

        if (type === SpeedType.WALK) _speed = if (_speed + 0.1f > 1f) _speed else _speed + 0.1f

        // TODO: Database implementation
        when (type) {
            SpeedType.FLY -> {
                this.getPlayer().flySpeed = _speed
            }
            SpeedType.WALK -> {
                this.getPlayer().walkSpeed = _speed
            }
        }
    }

    override fun setFlyModeEnabled(flyEnabled: Boolean) {
        this.player.allowFlight = flyEnabled
    }
}