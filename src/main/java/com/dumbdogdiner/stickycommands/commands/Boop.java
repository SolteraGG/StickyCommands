package com.dumbdogdiner.stickycommands.commands;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.arguments.APlayerArgument;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@Command("boop")
public class Boop {
    @Default
    public static void boop(Player me, @APlayerArgument Player them){

    }
    @Default
    public static void boop(ConsoleCommandSender me, @APlayerArgument Player them){

    }
}
