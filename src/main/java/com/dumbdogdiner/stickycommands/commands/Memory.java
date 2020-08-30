package com.dumbdogdiner.stickycommands.commands;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import com.dumbdogdiner.stickycommands.Main;
import com.ristexsoftware.knappy.bukkit.command.AsyncCommand;
import com.ristexsoftware.knappy.translation.LocaleProvider;
import com.ristexsoftware.knappy.util.TimeUtil;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Memory extends AsyncCommand {
    LocaleProvider locale = Main.getInstance().getLocaleProvider();

    public Memory(Plugin owner) {
        super("memory", owner);
        setPermission("stickycommands.memory");
        setDescription("Check the server's performance");
        setAliases(Arrays.asList("lag,elag,egc,mem,emem,memory,ememory,uptime,euptime,tps,etps,entities,eentities".split(",")));
    }

    @Override
    public int executeCommand(CommandSender sender, String commandLabel, String[] args) {
        try {
            if (!sender.hasPermission("stickycommands.memory"))
                return 2;
            if (!(sender instanceof Player)) {
                sender.sendMessage(locale.translate("must-be-player", new TreeMap<String, String>()));
                return 0;
            }
            Player player = (Player) sender;
            TreeMap<String, String> variables = locale.newVariables();
            DecimalFormat df = new DecimalFormat("0.0");

            double max = Runtime.getRuntime().maxMemory() / 1024 / 1024;
            double used = Runtime.getRuntime().totalMemory() / 1024 / 1024 - Runtime.getRuntime().freeMemory() / 1024 / 1024;
            double usage = Double.valueOf(df.format(((used / max) * 100)));
            char color = usage < 60 ? 'a' : (usage < 85 ? 'e' : 'c');
            double[] tps = Main.getInstance().getRecentTps();
            variables.put("tps_1m", String.valueOf(df.format(tps[0])));
            variables.put("tps_5m", String.valueOf(df.format(tps[1])));
            variables.put("tps_15m", String.valueOf(df.format(tps[2])));
            variables.put("max_memory", String.valueOf(max));
            variables.put("used_memory", String.valueOf(used));
            variables.put("memory_bar", ChatColor.translateAlternateColorCodes('&', "&f[&" + color + createBar(25, usage) + "&f]"));
            variables.put("loaded_chunks", String.valueOf(player.getWorld().getLoadedChunks().length));
            variables.put("entities", String.valueOf(player.getWorld().getEntities().size()));
            variables.put("world", player.getWorld().getName());
            System.out.println(Main.getInstance().getUpTime());
            System.out.println(System.currentTimeMillis());
            System.out.println(new Timestamp(Main.getInstance().getUpTime()));
            System.out.println(new Timestamp(System.currentTimeMillis()));
            variables.put("uptime", String.valueOf(TimeUtil.getUnixTime() - Main.getInstance().getUpTime()));
            variables.put("uptime_long", String.valueOf(Main.getInstance().getUpTime()));
            sender.sendMessage(locale.translate("memory-message", variables));
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    @Override
    public void onSyntaxError(CommandSender sender, String label, String[] args) {
        TreeMap<String, String> vars = locale.newVariables();
        vars.put("syntax", "/memory");
        sender.sendMessage(locale.translate("invalidSyntax", vars));
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
        return null;
    }

    public String createBar(double size, double usage) {
        double barCount = ((usage / 100) * size);
        String bar = "";
        for (double i = 0; i < size; i++) {
            if (i < barCount && size - i > 5)
                bar += "▍";
            else {
                if (size - i == 5) {
                    bar += usage + "%";
                    break;
                }
                bar += " ";
            }
        }
        return bar;
    }
}