package com.dumbdogdiner.stickycommands.commands;

import java.util.TreeMap;

import com.dumbdogdiner.stickycommands.StickyCommands;
import com.dumbdogdiner.stickycommands.User;
import com.dumbdogdiner.stickyapi.bukkit.command.AsyncCommand;
import com.dumbdogdiner.stickyapi.bukkit.command.ExitCode;
import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider;

import com.dumbdogdiner.stickycommands.utils.Constants;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Command("afk")
public class AfkCommand {
    private static StickyCommands plugin;
    private static LocaleProvider locale;
    public AfkCommand(StickyCommands plugin) {
        AfkCommand.plugin = plugin;
        AfkCommand.locale = plugin.getLocaleProvider();
    }

    @Default
    @Permission(Constants.Permissions.AFK)
    public static void afk(Player player) {
        TreeMap<String, String> vars = locale.newVariables();
        vars.put("player", player.getName());

        User u = StickyCommands.getInstance().getOnlineUser(player.getUniqueId());
        u.toggleAfk();
    }
}
