package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.bukkit.command.AsyncCommand;
import com.dumbdogdiner.stickyapi.bukkit.command.ExitCode;
import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider;
import com.dumbdogdiner.stickycommands.StickyCommands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.TreeMap;

public class Hat extends AsyncCommand{
    private LocaleProvider locale;
    private TreeMap<String, String> variables = locale.newVariables();
    private static final  String HAT_PERMISSION = "stickycommands.hat";

    /**
     * Create a new command for the associated plugin
     */
    public Hat(Plugin owner) {
        super("Hat", owner);
        locale = StickyCommands.getInstance().getLocaleProvider();
        setPermission(HAT_PERMISSION);
        setDescription("Wear an item as a hat (Swaps your helmet with what's in your offhand)");
        variables.put("syntax", "/hat");
    }

    @Override
    public ExitCode executeCommand(CommandSender sender, String commandLabel, String[] args) {
        if(!(sender instanceof Player)){
            return ExitCode.EXIT_MUST_BE_PLAYER;
        } else if(sender.hasPermission(HAT_PERMISSION)){
            PlayerInventory inv = ((Player) sender).getInventory();
            ItemStack oldHelmet = inv.getHelmet();
            ItemStack handItem = inv.getItemInMainHand();
            inv.setItemInMainHand(oldHelmet);
            inv.setHelmet(handItem);
            return ExitCode.EXIT_SUCCESS;
        } else {
            return ExitCode.EXIT_PERMISSION_DENIED;
        }
    }
}
