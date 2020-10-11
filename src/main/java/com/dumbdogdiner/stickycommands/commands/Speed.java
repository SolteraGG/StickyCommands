package com.dumbdogdiner.stickycommands.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import com.dumbdogdiner.stickycommands.Main;
import com.dumbdogdiner.stickycommands.SpeedType;
import com.dumbdogdiner.stickycommands.User;
import com.ristexsoftware.koffee.arguments.Arguments;
import com.ristexsoftware.koffee.bukkit.command.AsyncCommand;
import com.ristexsoftware.koffee.translation.LocaleProvider;
import com.ristexsoftware.koffee.util.NumberUtil;

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
        // a.optionalString("type");

        if (!a.valid())
            return 1;

        if (!(a.get("speed").matches("\\d*\\.?\\d+")))
            return 1;

        var speed = Float.parseFloat(a.get("speed")) / 10;
        if (speed*10> 10 || speed*10 <= 0)
            return 1;
        
        // if (a.exists("type")) {
        //     switch(a.get("type").toLowerCase()) {
        //         case "w":
        //         case "walk":
        //         case "walking":
        //             setSpeed(user, SpeedType.WALK, speed);
        //             break;
        //         case "f":
        //         case "fly":
        //         case "flight":
        //             setSpeed(user, SpeedType.FLY, speed);
        //             break;
        //     }
        // } else 
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
        // if (args.length < 2) {
        //     return Arrays.asList(new String[] {
        //         "fly",
        //         "walk"
        //     });
        // }
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

    // Look, I know the code below is pretty gnarly, but believe me it's 100x better than my solution before, and the code below is actually to ensure bukkit doesn't complain
    // so in a way this is very good to do.
    // To explain the... mess...
    // I need to check if the speed value is greater than 1, but also less than 0.1, if it's greater/less then we need to set a default value
    // However! The walk speed is fucked in bukkit, so 0.1 is actually slower than the default, so we have to check if the argument is 0.1 and change it to 0.2
    // You may ask, why use a ternary statement? Because I think it looks better, and I happen to understand them. I formatted them so whoever reads this can hopefully understand them
    void setSpeed(User user, SpeedType type, Float speed) {
        switch(type) {
            case WALK:
                user.setSpeed(SpeedType.WALK, speed < 1.9F ? 
                 (speed == 0.1F ? 
                    0.2F 
                    : (speed > 0.1F ? 
                        speed 
                        : 0.1F)) 
                 : 1F);
                break;
            case FLY:
                user.setSpeed(SpeedType.FLY, speed < 1.9F ? 
                (speed > 0F ? 
                    speed 
                    : 0.1F) 
                : 1F);
                break;
        }
    }
}
