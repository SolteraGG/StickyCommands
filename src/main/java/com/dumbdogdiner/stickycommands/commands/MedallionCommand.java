package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickycommands.managers.MedallionManager;
import com.dumbdogdiner.stickycommands.utils.Constants;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Subcommand;
import org.bukkit.command.CommandSender;

@Command(Constants.Commands.MEDALLION)
public class MedallionCommand {
    @Default
    public static void syntax(CommandSender sender){

    }
    @Subcommand("list")
    public static void listMedallions(CommandSender sender){
        //MedallionManager
    }
}
