package com.dumbdogdiner.stickycommands.commands;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import com.dumbdogdiner.stickycommands.Main;
import com.dumbdogdiner.stickycommands.SpeedType;
import com.dumbdogdiner.stickycommands.User;
import com.ristexsoftware.koffee.arguments.Arguments;
import com.ristexsoftware.koffee.bukkit.command.AsyncCommand;
import com.ristexsoftware.koffee.translation.LocaleProvider;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Speed extends AsyncCommand {

    LocaleProvider locale = Main.getInstance().getLocaleProvider();
    TreeMap<String, String> variables = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
    public Speed(Plugin owner) {
        super("speed", owner);
        setPermission("stickycommands.speed");
        setDescription("Change your fly or walk speed");
    }
    
    @Override
    public int executeCommand(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player))
            return 2;

        User user = Main.getInstance().getOnlineUser(((Player)sender).getUniqueId());
        Arguments a = new Arguments(args);
        a.requiredString("speed");

        if (!a.valid())
            return 1;

        if (!(a.get("speed").matches("\\d*\\.?\\d+")))
            return 1;

        var speed = Float.parseFloat(a.get("speed")) / 10;
        if (speed*10> 10 || speed*10 <= 0)
            return 1;

        if (((Player)sender).isFlying()) {
            setSpeed(user, SpeedType.FLY, speed);
        } else {
            setSpeed(user, SpeedType.WALK, speed);
        }
        variables.put("speed", a.get("speed"));
        sender.sendMessage(locale.translate("speed-message", variables));
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

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (args.length < 2) {
            return Arrays.asList(new String[] {
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "10"
            });
        }
        return null;
    }


    void setSpeed(User user, SpeedType type, Float speed) {
        user.setSpeed(type, speed < 1.9F 
        ? (speed > 0F
            ? (type == SpeedType.FLY
                    ? speed
                    : speed + 0.1F > 1F
                        ? speed
                        : speed + 0.1F)
            : 0.1F) 
        : 1F);
    }
}
