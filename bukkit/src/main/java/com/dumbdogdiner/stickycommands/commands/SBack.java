package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil;
import com.dumbdogdiner.stickycommands.StickyCommands;
import com.dumbdogdiner.stickycommands.WithPlugin;
import com.dumbdogdiner.stickycommands.util.WorldLocation;
import com.google.common.base.Preconditions;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.arguments.APlayerArgument;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.logging.Logger;

import static java.text.MessageFormat.format;



// I hate like everything about the kotlin bodging but I'll fix it after I do a fuckload of testing

@Command("SBack")
public class SBack {

    private static final String PERM = "stickycommands.sback";
    private static final String PERM_OTHERS = "stickycommands.sback.others";


    @Permission(PERM)
    @Default
    public void goBack(@NotNull CommandSender sender) {
        try {
            if (sender instanceof ProxiedCommandSender) {
                Preconditions.checkArgument(((ProxiedCommandSender) sender).getCaller().hasPermission(PERM), format("The caller {0} doesn't have the required permission {1}", ((ProxiedCommandSender) sender).getCaller().getName(), PERM));
                sender = ((ProxiedCommandSender) sender).getCallee();
            }
            Preconditions.checkState(sender instanceof Player, format("Command was not called on a player (was called on {0}"), sender.getName());
            UUID uuid = ((Player) sender).getUniqueId();

            // TODO: ideally getting particular bits should be in postgres handler not like this!
            Location to = StickyCommands.getPlugin().getPostgresHandler().getUserLocation(uuid);

            ((Player) sender).teleport(to);


        } catch (IllegalArgumentException | IllegalStateException e) {
            sender.sendMessage(e.getMessage());
            if (sender instanceof Player) {
                SoundUtil.queueSound((Player) sender, Sound.ENTITY_ITEM_BREAK, 5, 1, 0L);
            }
        }
    }

    @Permission(PERM_OTHERS)
    @Default
    public void goBack(CommandSender sender, @APlayerArgument String player) {
        Preconditions.checkArgument(sender.hasPermission(PERM));
        Preconditions.checkNotNull(Bukkit.getPlayer(player));
        goBack(Bukkit.getPlayer(player));
    }
}
