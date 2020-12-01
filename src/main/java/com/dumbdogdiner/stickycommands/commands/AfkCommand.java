package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.bukkit.command.StickyPluginCommand;
import com.dumbdogdiner.stickyapi.bukkit.command.tabcomplete.NonCompletingTabExecutor;
import com.dumbdogdiner.stickyapi.common.arguments.Arguments;
import com.dumbdogdiner.stickyapi.common.command.ExitCode;
import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider;
import com.dumbdogdiner.stickycommands.StickyCommands;
import com.dumbdogdiner.stickycommands.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AfkCommand extends StickyPluginCommand {


    
    public AfkCommand() {

        super("afk", null, StickyCommands.getInstance());
        setPermission("stickycommands.afk");
        setDescription("Let the server know you're afk!");
        usageMessage = "Syntax: /afk"; //todo fixme
    }


    public static void setAFKAndBroadcast(User user, boolean afk){
        TreeMap<String, String> variables = LocaleProvider.newVariables();
        variables.put("player", user.getName());
        variables.put("player_uuid", user.getUniqueId().toString());
        user.setAfk(afk);

        if(!user.isHidden()){
            if(user.isAfk()){
                Bukkit.getServer().broadcastMessage(StickyCommands.getInstance().getLocaleProvider().translate("afk.afk", variables));
                // Bukkit is literally fucking retarded, and I can't use broadcastMessage because that magically doesn't work now! Who knew....
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(StickyCommands.getInstance().getLocaleProvider().translate("afk.afk", variables));
                }
            } else {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(StickyCommands.getInstance().getLocaleProvider().translate("afk.not-afk", variables));
                }
            }
        }
    }

    @Override
    public ExitCode execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull Arguments arguments, @NotNull Map<String, String> variables) {
        if (!(commandSender instanceof Player))
            return ExitCode.EXIT_MUST_BE_PLAYER;

        User user = StickyCommands.getInstance().getOnlineUser(((Player)commandSender).getUniqueId());
        variables.put("player", user.getName());
        variables.put("player_uuid", user.getUniqueId().toString());

        setAFKAndBroadcast(user, !user.isAfk());
        return ExitCode.EXIT_SUCCESS;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) throws IllegalArgumentException, CommandException {
        return new NonCompletingTabExecutor().tabComplete(commandSender, s, new Arguments(List.of(strings)));
    }
}
