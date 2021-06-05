package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.bukkit.item.WrittenBookBuilder;
import com.dumbdogdiner.stickyapi.bukkit.util.SoundUtil;
import com.dumbdogdiner.stickycommands.StickyCommands;
import com.dumbdogdiner.stickycommands.utils.Constants;
import com.dumbdogdiner.stickycommands.utils.ResourceUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.jorel.commandapi.annotations.Alias;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

@Command(Constants.Commands.RULEBOOK)
@Alias({Constants.Commands.RULES})
public class RulebookCommand {
    private static final ItemStack rulebookStack;
    static{
        File bookFile = ResourceUtils.getOrCreate(Constants.Files.RULEBOOK);
        JsonObject bookJson;
        try {
            bookJson = new JsonParser().parse(new FileReader(bookFile)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            StickyCommands.getInstance().getLogger().severe("External rulebook not found, trying internal");
            bookJson = new JsonParser().parse(new InputStreamReader(Objects.requireNonNull(StickyCommands.getInstance().getResource(Constants.Files.RULEBOOK)))).getAsJsonObject();
        }
        rulebookStack = WrittenBookBuilder.fromJson(bookJson).toItemStack(1);
    }

    @Default
    public static void rulebook(Player player){
        if(player.getInventory().firstEmpty() != -1){
            player.getInventory().addItem(rulebookStack);
            //SoundUtil.sendSuccess(player);
            //SoundUtil.sendInfo(player);
            SoundUtil.queueSound(player, Sound.ENTITY_ITEM_PICKUP, 1f, 1f, 0);
        } else {
            // todo locale
            player.sendMessage("You don't have space for this");
            SoundUtil.sendError(player);
        }
    }
}
