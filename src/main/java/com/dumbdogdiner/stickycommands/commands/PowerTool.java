package com.dumbdogdiner.stickycommands.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.dumbdogdiner.stickycommands.Main;
import com.google.common.base.Joiner;
import com.ristexsoftware.knappy.bukkit.command.AsyncCommand;
import com.ristexsoftware.knappy.translation.LocaleProvider;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class PowerTool extends AsyncCommand {
    LocaleProvider locale = Main.getInstance().getLocaleProvider();

    public PowerTool(Plugin owner) {
        super("powertool", owner);
        setPermission("stickycommands.powertool");
        setDescription("");
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
    public int executeCommand(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission("stickycommands.powertool"))
            return 2;

        TreeMap<String, String> variables = locale.newVariables();
        Player player = (Player) sender;
        try {
            if (args.length < 1) {
                variables.put("player", player.getName());
                if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                    getPowerTool(player, null, true);
                    sender.sendMessage(locale.translate("powertool.cleared", variables));
                }
            } else {
                String s = Joiner.on(" ").join(args);
                variables.put("command", s);
                if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                    getPowerTool(player, s, false);
                    sender.sendMessage(locale.translate("powertool.assigned", variables));
                    return 0;
                }
                sender.sendMessage("You cannot bind air to a command!");
            }
        } catch (Exception e) {
            return 1;
        }
        return 0;
    }

    private ItemStack getPowerTool(Player player, String command, boolean clear) {
        ItemStack is = player.getInventory().getItemInMainHand();
        ItemMeta meta = is.getItemMeta();
        List<String> lore = new ArrayList<String>();
        if (clear)
            lore.clear();
        else
            lore.add("command:" + command);
        meta.setLore(lore);
        is.setItemMeta(meta);
        return is;
    }
}