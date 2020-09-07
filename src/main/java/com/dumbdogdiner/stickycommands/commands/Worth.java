package com.dumbdogdiner.stickycommands.commands;

import java.util.TreeMap;

import com.dumbdogdiner.stickycommands.Main;
import com.dumbdogdiner.stickycommands.utils.Item;
import com.ristexsoftware.knappy.bukkit.command.AsyncCommand;
import com.ristexsoftware.knappy.translation.LocaleProvider;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Worth extends AsyncCommand {
    static Main self = Main.getInstance();
    LocaleProvider locale = Main.getInstance().getLocaleProvider();
    TreeMap<String, String> variables = locale.newVariables();
    
    public Worth(Plugin owner) {
        super("worth", owner);
        setDescription("Check the worth of an item.");
        setPermission("stickycommands.worth");
        variables.put("syntax", "/worth [hand/inventory]");
    }

    @Override
    public int executeCommand(CommandSender sender, String commandLabel, String[] args) {
        try {
            if (!sender.hasPermission("stickycommands.worth") || (!(sender instanceof Player)))
                return 2;
            
            var player = (Player) sender;
            var item = new Item(player.getInventory().getItemInMainHand());
            ItemStack[] inventory = player.getInventory().getContents();
            variables.put("player", player.getName());
            variables.put("item", item.getName());

            if (item.getAsItemStack().getType() == Material.AIR) {
                sender.sendMessage(locale.translate("cannot-sell", variables));
                return 0;
            }
            
            var worth = item.getWorth();
            var itemAmount = 0;
            for (var is : inventory) {
                if (is != null && is.getType() == item.getType()) {
                    itemAmount += is.getAmount();
                }
            }
            variables.put("single_worth", Double.toString(worth));
            variables.put("hand_worth", Double.toString(worth * item.getAmount()));
            variables.put("inventory_worth", Double.toString(worth * itemAmount));
            
            if (worth != 0.0) {
                sender.sendMessage(locale.translate("worth-message", variables));
                return 0;
            }
            
            sender.sendMessage(locale.translate("cannot-sell", variables));
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    @Override
    public void onSyntaxError(CommandSender sender, String label, String[] args) {

    }

    @Override
    public void onPermissionDenied(CommandSender sender, String label, String[] args) {
        sender.sendMessage(locale.translate("no-permission", variables));
    }

    @Override
    public void onError(CommandSender sender, String label, String[] args) {
        sender.sendMessage(locale.translate("server-error", variables));
    }
}
