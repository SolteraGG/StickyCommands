package com.dumbdogdiner.stickycommands.commands;

import java.util.TreeMap;

import com.dumbdogdiner.stickycommands.Main;
import com.dumbdogdiner.stickycommands.utils.Item;
import com.ristexsoftware.knappy.arguments.Arguments;
import com.ristexsoftware.knappy.bukkit.command.AsyncCommand;
import com.ristexsoftware.knappy.translation.LocaleProvider;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Sell extends AsyncCommand {
    static Main self = Main.getInstance();
    LocaleProvider locale = Main.getInstance().getLocaleProvider();
    TreeMap<String, String> variables = locale.newVariables();
    
    public Sell(Plugin owner) {
        super("sell", owner);
        setDescription("Check the worth of an item.");
        setPermission("stickycommands.worth");
        variables.put("syntax", "/worth");
    }

    @Override
    public int executeCommand(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission("stickycommands.sell") || (!(sender instanceof Player)))
            return 2;

        Arguments a = new Arguments(args);
        a.optionalString("sellMode");

        var player = (Player) sender;
        var item = new Item(player.getInventory().getItemInMainHand());
        ItemStack[] inventory = player.getInventory().getContents();
        variables.put("player", player.getName());
        variables.put("item", item.getName());

        if (item.getAsItemStack().getType() == Material.AIR) {
            sender.sendMessage(locale.translate("cannot-sell", variables));
            return 0;
        }

        var inventoryAmount = 0;
        for (var is : inventory) {
            if (is != null && is.getType() == item.getType() && is != item.getAsItemStack()) {
                inventoryAmount += is.getAmount();
            }
        }
        var worth = item.getWorth();
        // var itemAmount = inventoryAmount-handAmount;
        System.out.println(inventoryAmount);
        variables.put("single_worth", Double.toString(worth));
        variables.put("hand_worth", Double.toString(worth * item.getAmount()));
        variables.put("inventory_worth", Double.toString(worth * inventoryAmount));
        
        if (worth != 0.0) {
            if (!a.exists("sellMode") || a.get("sellMode").equalsIgnoreCase("hand")) {
                variables.put("amount", String.valueOf(item.getAmount()));
                variables.put("worth", String.valueOf(item.getWorth() * item.getAmount()));
                Main.getInstance().getEconomy().depositPlayer(player, worth * item.getAmount());
                player.getInventory().getItemInMainHand().setAmount(0);
                player.sendMessage(locale.translate("sell-message", variables));
                return 0;
            }

            switch (a.get("sellMode").toLowerCase()) {
                case "inventory":
                case "invent":
                case "inv":
                    Main.getInstance().getEconomy().depositPlayer(player, worth * inventoryAmount);
                    variables.put("amount", String.valueOf(inventoryAmount));
                    variables.put("worth", String.valueOf(item.getWorth() * inventoryAmount));
                    player.sendMessage(locale.translate("sell-message", variables));
                    consumeItem(player, inventoryAmount, item.getType());
                    return 0;                    
            }
            sender.sendMessage(locale.translate("worth-message", variables));
            return 0;
        }
        
        sender.sendMessage(locale.translate("cannot-sell", variables));
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
    
    public boolean consumeItem(Player player, int count, Material mat) {
        ItemStack[] item = player.getInventory().getContents();

        for (ItemStack s : item) {
            if (s != null) {
                if (s.getType() == mat) {
                    s.setAmount(0);
                }
            }
        }

        player.updateInventory();
        return true;
    }
}