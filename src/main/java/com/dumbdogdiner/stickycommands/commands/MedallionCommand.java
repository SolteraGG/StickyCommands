package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickycommands.utils.Constants;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import org.bukkit.command.CommandSender;

@Command(Constants.Commands.MEDALLION)
public class MedallionCommand {
    @Default
    public void syntax(CommandSender sender){

    }
}
