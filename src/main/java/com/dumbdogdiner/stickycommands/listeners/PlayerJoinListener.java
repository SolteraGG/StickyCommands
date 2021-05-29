package com.dumbdogdiner.stickycommands.listeners;

import com.dumbdogdiner.stickyapi.common.util.TimeUtil;
import com.dumbdogdiner.stickycommands.StickyCommands;
import com.dumbdogdiner.stickycommands.User;

import com.dumbdogdiner.stickycommands.database.PostgresHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles logic relating to players joining and leaving the server.
 */
public class PlayerJoinListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        //StickyCommands.getDatabaseHandler().updateUser(event.getPlayer(), false);
        StickyCommands.getOnlineUserCache().put(event.getPlayer().getUniqueId(), new User(event.getPlayer()));
//        var player = event.getPlayer();
//        StickyCommands.getInstance().getOnlineUserCache().put(player.getUniqueId(), User.fromPlayer(player));
//        StickyCommands.getDatabaseHandler().updateUser(player.getUniqueId().toString(), player.getName(), player.getAddress().getAddress().getHostAddress(), TimeUtil.now(), true, true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        StickyCommands.getDatabaseHandler().updateUser(event.getPlayer(), true);
//        var player = event.getPlayer();
//        StickyCommands.getInstance().getOnlineUserCache().remove(player.getUniqueId());
//        StickyCommands.getInstance().getDatabaseHandler().updateUser(player.getUniqueId().toString(), player.getName(), player.getAddress().getAddress().getHostAddress(), TimeUtil.now(), false, false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerKickEvent event) {
        StickyCommands.getDatabaseHandler().updateUser(event.getPlayer(), true);
//        var player = event.getPlayer();
//        StickyCommands.getInstance().getOnlineUserCache().remove(player.getUniqueId());
//        StickyCommands.getInstance().getDatabaseHandler().updateUser(player.getUniqueId().toString(), player.getName(), player.getAddress().getAddress().getHostAddress(), TimeUtil.now(), false, false);
    }
}
