package com.dumbdogdiner.stickycommands.commands;

import java.util.List;
import java.util.TreeMap;

import com.dumbdogdiner.stickycommands.Main;
import com.dumbdogdiner.stickycommands.utils.LocationUtil;
import com.ristexsoftware.knappy.bukkit.command.AsyncCommand;
import com.ristexsoftware.knappy.translation.LocaleProvider;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

public class Jump extends AsyncCommand {
    private static LocaleProvider locale = Main.getInstance().getLocaleProvider();

    public Jump(Plugin owner) {
        super("jump", owner);
    }

    @Override
    public int executeCommand(CommandSender sender, String commandLabel, String[] args) {
        // TODO handle
        try {
            if (!(sender instanceof Player)) {
                sender.sendMessage(locale.translate("must-be-player", new TreeMap<String, String>()));
                return 0;
            }
    
            Player player = (Player) sender;
            Location loc = null;
            
            try {
                loc = LocationUtil.getSafeDestination(LocationUtil.getTarget(player));
            } catch (Exception e) {
                e.printStackTrace();
                return 1;
            }
            
            if (loc.getBlock().getType() == Material.AIR)
                loc.setY(player.getWorld().getHighestBlockYAt(loc));
            
            loc.setYaw(player.getLocation().getYaw());
            loc.setPitch(player.getLocation().getPitch());
            loc.add(0, 1, 0);
            final Location syncLoc = loc;
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> player.teleport(syncLoc), 1L);
            TreeMap<String, String> variables = locale.newVariables();
            variables.put("player", player.getName());
            variables.put("x", String.valueOf(loc.getX()));
            variables.put("y", String.valueOf(loc.getY()));
            variables.put("z", String.valueOf(loc.getZ()));
            
            sender.sendMessage(locale.translate("jump-message", variables));
    
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    @Override
    public void onSyntaxError(CommandSender sender, String label, String[] args) {
        
    }
    
    @Override
    public void onPermissionDenied(CommandSender sender, String label, String[] args) {
        sender.sendMessage(locale.get("permissionDenied"));
    }

    @Override
    public void onError(CommandSender sender, String label, String[] args) {
        sender.sendMessage(locale.get("serverError"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return null; // We don't want any tab complete for this command
    }
}