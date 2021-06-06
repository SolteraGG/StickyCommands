package com.dumbdogdiner.stickycommands.managers;

import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider;
import com.dumbdogdiner.stickycommands.StickyCommands;
import com.dumbdogdiner.stickycommands.User;
import com.dumbdogdiner.stickycommands.utils.Constants;
import com.dumbdogdiner.stickycommands.utils.ItemDeserializer;
import com.dumbdogdiner.stickycommands.utils.ResourceUtils;
import com.google.common.base.Predicate;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handles giving players medallions if the feature is enabled
 */
public class MedallionManager implements Listener {
    private final StickyCommands pluginInstance;
    private static final ItemDeserializer medallionItems = new ItemDeserializer(Constants.Files.MEDALLION_ITEMS);

    private static Map<String /* Medallion */, List<UUID>> medallionsList;

    public MedallionManager(@NotNull StickyCommands pluginInstance) {
        this.pluginInstance = pluginInstance;

        File medallionFile = ResourceUtils.getOrCreate(Constants.Files.MEDALLION_UUIDS);
         medallionsList = new HashMap<>();
        // This can be cleaned up by using an sqlite db or importing into postgres directly or similar
        // for now its fine
        try {
            Yaml y = new Yaml();
            Map<String, List<String>> players = (Map<String, List<String>>) y.loadAs(new FileReader(medallionFile), Map.class);
            for(String medallion : players.keySet()){
                medallionsList.put(medallion, players.get(medallion).stream().map(UUID::fromString).collect(Collectors.toList()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reloadMedallions(){
        medallionItems.reloadItems();
    }



    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event){
        // todo: fix user class!
        Player p = event.getPlayer();
        User u = User.fromPlayer(p);
        if(!u.isFirstJoinItemsGiven()) {
            u.setFirstJoinItemsGiven(true);
            List<String> medallionsToGive = new ArrayList<>();
            for(String medallion : medallionsList.keySet()){
                if(medallionsList.get(medallion).contains(p.getUniqueId()))
                    medallionsToGive.add(medallion);
            }
            if((Arrays.stream(p.getInventory().getContents()).filter((Predicate<ItemStack>) Objects::isNull).count() <= medallionsToGive.size())) {
                Map<String, String> vars = LocaleProvider.newVariables();
                vars.put("SLOTS", Integer.toString(medallionsToGive.size()));
                p.sendMessage(pluginInstance.getLocaleProvider().translate("medallions.no-space", vars));
                u.setFirstJoinItemsGiven(false);
            }

             for(String medallion : medallionsToGive){
                 ItemStack i = medallionItems.get(medallion).asOne();
                 ItemMeta m = i.getItemMeta();
                 List<Component> loreComps = m.lore();
                 assert loreComps != null;
                 TextColor c = loreComps.get(0).color();

                 // Add in reverse order
                 loreComps.add(0, Component.text(""));
                 loreComps.add(0, Component.text(""));
                 loreComps.add(0, Component.text(MessageFormat.format("To {0}:", p.getName())).color(c));
                 m.lore(loreComps);
                 i.setItemMeta(m);
                p.getInventory().addItem(i);
             }
        }
    }
}
