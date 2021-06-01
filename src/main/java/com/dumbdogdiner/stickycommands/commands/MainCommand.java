package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickycommands.StickyCommands;
import com.dumbdogdiner.stickycommands.utils.Constants;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import org.bukkit.command.CommandSender;

@Command("stickycommands")
//@Permission(Constants.Permissions.STICKYCOMMANDS)
public class MainCommand {
    @Default
    public static void info(CommandSender sender){
        sender.sendMessage("Stickycommands uwu");
    }

}
