package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.bukkit.command.AsyncCommand;
import com.dumbdogdiner.stickyapi.bukkit.command.ExitCode;
import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil;
import com.dumbdogdiner.stickyapi.common.arguments.Arguments;
import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider;
import com.dumbdogdiner.stickycommands.StickyCommands;
import com.dumbdogdiner.stickycommands.User;
import com.dumbdogdiner.stickycommands.utils.Constants;
import com.dumbdogdiner.stickycommands.utils.SpeedType;
import com.dumbdogdiner.stickycommands.utils.VariableUtils;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.arguments.AIntegerArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

@Command(Constants.Commands.SPEED)
@Permission(Constants.Permissions.SPEED)
public class SpeedCommand {
    static StickyCommands instance = StickyCommands.getInstance();
    static LocaleProvider locale = instance.getLocaleProvider();

    public static void speed(Player player, @AIntegerArgument(min = 1, max = 10) int range) {
        var vars = locale.newVariables();
        VariableUtils.withPlayer(vars, player, false);
        var user = instance.getOnlineUser(player.getUniqueId());
        var flying = player.isFlying();
        var speed = range / 10f;
        user.setSpeed(flying ? SpeedType.FLY : SpeedType.WALK, speed);
        vars.put("player_speed", String.valueOf(speed));
        vars.put("player_flying", String.valueOf(flying));
        player.sendMessage(locale.translate(Constants.LanguagePaths.SPEED_MESSAGE, vars));
        SoundUtil.sendSuccess(player);
    }
}
