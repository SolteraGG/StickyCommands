package com.dumbdogdiner.stickycommands.commands;

import java.util.TreeMap;

import com.dumbdogdiner.stickycommands.Main;
import com.dumbdogdiner.stickycommands.User;
import com.ristexsoftware.koffee.bukkit.command.AsyncCommand;
import com.ristexsoftware.koffee.translation.LocaleProvider;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.chat.TextComponent;

public class Afk extends AsyncCommand {

    private static LocaleProvider locale = Main.getInstance().getLocaleProvider();
    TreeMap<String, String> variables = locale.newVariables();
    
    public Afk(Plugin owner) {
        super("afk", owner);
        setPermission("stickycommands.afk");
        setDescription("Let the server know you're afk!");
        variables.put("syntax", "/afk");
    }

    // TODO: Clean this up
    @Override
    public int executeCommand(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player))
            return 2;
        User user = Main.getInstance().getOnlineUser(((Player)sender).getUniqueId());
        variables.put("player", user.getName());
        variables.put("player_uuid", user.getUniqueId().toString());
        
        if (user.isAfk()) {
            user.setAfk(false);
            Bukkit.broadcastMessage(locale.translate("not-afk-message", variables));
            return 0;
        }
        user.setAfk(true);
        Bukkit.broadcastMessage(locale.translate("afk-message", variables));
        
        return 0;
    }

    @Override
    public void onSyntaxError(CommandSender sender, String label, String[] args) {
        sender.sendMessage(locale.translate("invalid-syntax", variables));
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
