package com.dumbdogdiner.stickycommands.listeners;

import com.dumbdogdiner.stickycommands.Main;
import com.dumbdogdiner.stickycommands.User;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles logic relating to players joining and leaving the server.
 */
class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Main.getInstance().getOnlineUserCache().put(User.fromPlayer(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Main.getInstance().getOnlineUserCache().removeKey(e.getPlayer().getUniqueId().toString());
    }
}
