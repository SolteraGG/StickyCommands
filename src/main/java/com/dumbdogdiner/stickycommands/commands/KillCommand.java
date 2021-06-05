package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider;
import com.dumbdogdiner.stickycommands.StickyCommands;
import com.dumbdogdiner.stickycommands.utils.Constants;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.arguments.AEntitySelectorArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument.EntitySelector;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Kill an entity
 */
@Command("killE")
@Permission(Constants.Permissions.KILL_ENTITIES)
public class KillCommand {
    private static LocaleProvider localeProvider;

    public KillCommand(StickyCommands instance){
        localeProvider = instance.getLocaleProvider();
        CommandAPI.registerCommand(getClass());
    }

    @Default
    public static void killEntities(CommandSender sender, @AEntitySelectorArgument(EntitySelector.MANY_ENTITIES)Collection<Entity> entities){
        Map<String, Integer> entityTypes = new HashMap<>();
        for(Entity e : entities) {
            if(!e.hasPermission(Constants.Permissions.KILL_IMMUNE)) {
                entityTypes.put(e.getName(), entityTypes.getOrDefault(e.getName(), 0) + 1);
                // Not sure if this will work
                e.sendMessage(localeProvider.translate("kill.you-were-killed", localeProvider.newVariables()));
                if (e instanceof LivingEntity)
                    ((LivingEntity) e).setHealth(0);
                else
                    e.remove();
            }
        }

        for(Map.Entry<String, Integer> entity : entityTypes.entrySet()){
            var vars = localeProvider.newVariables();
            vars.put("ENTITY", entity.getKey());
            vars.put("QUANTITY", String.valueOf(entity.getValue()));
            sender.sendMessage(localeProvider.translate("kill.you-killed-entities", vars));
        }
    }

    @Default
    public static void killEntity(CommandSender sender, @AEntitySelectorArgument(EntitySelector.ONE_ENTITY) Entity entity){
        killEntities(sender, Collections.singleton(entity));
    }
}