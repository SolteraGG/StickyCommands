package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.bukkit.item.WrittenBookBuilder;
import com.dumbdogdiner.stickycommands.utils.Constants;
import dev.jorel.commandapi.annotations.Alias;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Command(Constants.Commands.RULEBOOK)
@Alias({Constants.Commands.RULES})
public class RulebookCommand {
    @Default
    public static void rulebook(Player player){

    }
}
