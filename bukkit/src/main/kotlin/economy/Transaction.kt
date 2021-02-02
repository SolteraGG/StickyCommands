package com.dumbdogdiner.stickycommands.economy

import com.dumbdogdiner.stickycommands.api.economy.Transaction
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

class Transaction(
    private val player: Player,
    private val item: Material,
    private val quantity: Int
): Transaction {



    override fun getId(): Int {
        TODO("Not yet implemented")
    }

    override fun getPlayer(): OfflinePlayer {
        TODO("Not yet implemented")
    }

    override fun getItemType(): Material {
        TODO("Not yet implemented")
    }

    override fun getItemQuantity(): Int {
        TODO("Not yet implemented")
    }

    override fun getSalePrice(): Int {
        TODO("Not yet implemented")
    }

    override fun getNewBalance(): Double {
        TODO("Not yet implemented")
    }

    override fun getDate(): Date {
        TODO("Not yet implemented")
    }
}