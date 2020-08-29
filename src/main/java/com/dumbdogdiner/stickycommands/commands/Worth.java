package com.dumbdogdiner.stickycommands.commands;

import com.ristexsoftware.knappy.bukkit.command.AsyncCommand;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class Worth extends AsyncCommand {

    public Worth(String commandName, Plugin owner) {
        super("worth", owner);
        setDescription("Check the worth of an item.");
        setPermission("stickycommands.worth");
    }

    @Override
    public void onSyntaxError(CommandSender sender, String label, String[] args) {
    }

    @Override
    public void onPermissionDenied(CommandSender sender, String label, String[] args) {

    }

    @Override
    public void onError(CommandSender sender, String label, String[] args) {

    }

    @Override
    public int executeCommand(CommandSender sender, String commandLabel, String[] args) {

        return 0;
    }
    
}
