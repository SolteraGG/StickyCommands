package com.dumbdogdiner.stickycommands;

import com.dumbdogdiner.stickycommands.utils.Constants;
import com.google.common.base.Predicate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 * Handles giving players medallions if the feature is enabled
 */
public class MedallionManager implements Listener {
    private final StickyCommands pluginInstance;

    private Map<UUID, List<Integer>> medallionsList = new HashMap<>();
    private List<Function<String, ItemStack>> medallions;

    public MedallionManager(@NotNull StickyCommands pluginInstance) {
        this.pluginInstance = pluginInstance;

        File medallionFile = new File(pluginInstance.getDataFolder(), Constants.Files.MEDALLION_UUIDS);
        if(!medallionFile.exists()) {
            medallionFile.getParentFile().mkdirs();
            pluginInstance.saveResource(Constants.Files.MEDALLION_UUIDS, false);
        }

        // This can be cleaned up by using an sqlite db or importing into postgres directly or similar
        // for now its fine
        try {
            CSVParser medallionParser = CSVParser.parse(medallionFile, StandardCharsets.US_ASCII, CSVFormat.EXCEL);
            List<String> csvHeader = medallionParser.getHeaderNames();

            for(CSVRecord line : medallionParser.getRecords()){
                UUID id = UUID.fromString(line.get(csvHeader.get(0)));
                int season = Integer.parseInt(line.get(csvHeader.get(1)).substring(1));
                if(!medallionsList.containsKey(id)){
                    medallionsList.put(id, new ArrayList<>());
                }
                medallionsList.get(id).add(season);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        medallions = generateMedallionList();

        Bukkit.getPluginManager().registerEvents(this,pluginInstance);
    }

    // For now we are going to hardcode the medallion list

    private static List<Function<String, ItemStack>> generateMedallionList(){
        List<Function<String, ItemStack>> medallionList = new ArrayList<>();

        medallionList.add(name -> {
            ItemStack m1 = new ItemStack(Material.STRUCTURE_BLOCK, 1);
            ItemMeta m1Meta = m1.getItemMeta();
            m1Meta.setUnbreakable(true);
            m1Meta.displayName(Component.text("Season 1 Medallion").color(TextColor.fromCSSHexString("#ff66d8")));

            // less annoying but also annoying way
            TextColor m1LoreColor = TextColor.fromCSSHexString("#66c7f4");
            List<Component> m1Lore = new ArrayList<>();
            m1Lore.add(Component.text("To " + name + ":").color(m1LoreColor));
            m1Lore.add(Component.text(""));
            m1Lore.add(Component.text(""));
            m1Lore.add(Component.text("This special token recognizes").color(m1LoreColor));
            m1Lore.add(Component.text("your unending dedication to the").color(m1LoreColor));
            m1Lore.add(Component.text("Dumb Dog Diner Minecraft server").color(m1LoreColor));
            m1Lore.add(Component.text("network ever since the very beginning!").color(m1LoreColor));
            m1Lore.add(Component.text(""));
            m1Lore.add(Component.text(""));
            m1Lore.add(Component.text("The people of ").color(m1LoreColor)
                    .append(Component.text("Spawntown ").color(NamedTextColor.GOLD)
                            .append(Component.text("are forever").color(m1LoreColor))));
            m1Lore.add(Component.text("grateful, and proud to be able to call").color(m1LoreColor));
            m1Lore.add(Component.text("you an important part of our family!").color(m1LoreColor));
            m1Lore.add(Component.text(""));
            m1Lore.add(Component.text(""));
            m1Lore.add(Component.text("- Stixil").color(TextColor.fromCSSHexString("#6c6ea0")).decoration(TextDecoration.ITALIC, true));

            m1Meta.lore(m1Lore);
            m1Meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);

            m1.setItemMeta(m1Meta);
            return m1;
        });

        // /give @p structure_block{Unbreakable:1,display:{Name:'[{"text":"Season 1 Medallion","italic":false,"color":"#ff66d8"}]',Lore:['[{"text":"This special token recognizes","italic":false,"color":"#66c7f4"}]','[{"text":"your unending dedication to the","italic":false,"color":"#66c7f4"}]','[{"text":"Dumb Dog Diner Minecraft server","italic":false,"color":"#66c7f4"}]','[{"text":"networ ever since the very beginning!","italic":false,"color":"#66c7f4"},{"text":"","italic":false,"color":"dark_purple"}]','[{"text":"","italic":false,"color":"dark_purple"}]','[{"text":"The people of ","italic":false,"color":"#66c7f4"},{"text":"Spawntown ","color":"gold"},{"text":"are forever","color":"#66c7f4"}]','[{"text":"grateful, and proud to be able to call","italic":false,"color":"#66c7f4"}]','[{"text":"you an important part of our family!","italic":false,"color":"#66c7f4"},{"text":"","italic":false,"color":"dark_purple"}]','[{"text":"","italic":false,"color":"dark_purple"}]','[{"text":"- Stixil","italic":false,"color":"#6c6ea0"}]']},Enchantments:[{id:infinity,lvl:1}]} 1

        medallionList.add(name -> {
            ItemStack m2 = new ItemStack(Material.REPEATING_COMMAND_BLOCK);
            ItemMeta m2Meta = m2.getItemMeta();
            m2Meta.setUnbreakable(true);
            m2Meta.displayName(Component.text("Season 2 Medallion").color(TextColor.fromCSSHexString("#7e2e88")));

            TextColor m2LoreColor = TextColor.fromCSSHexString("#d14081");
            List<Component> m2Lore = new ArrayList<>();
            m2Lore.add(Component.text("To " + name + ":").color(m2LoreColor));
            m2Lore.add(Component.text(""));
            m2Lore.add(Component.text(""));
            m2Lore.add(Component.text("This special token recognizes").color(m2LoreColor));
            m2Lore.add(Component.text("your long-standing dedication").color(m2LoreColor));
            m2Lore.add(Component.text("to the Dumb Dog Diner").color(m2LoreColor));
            m2Lore.add(Component.text("Minecraft server network").color(m2LoreColor));
            m2Lore.add(Component.text(""));
            m2Lore.add(Component.text(""));
            m2Lore.add(Component.text("Live together, shop").color(m2LoreColor));
            m2Lore.add(Component.text("together, fight together!").color(m2LoreColor));
            m2Lore.add(Component.text(""));
            m2Lore.add(Component.text(""));
            m2Lore.add(Component.text("- Stixil").color(TextColor.fromCSSHexString("#ff798a")).decoration(TextDecoration.ITALIC, true));

            m2Meta.lore(m2Lore);
            m2Meta.addEnchant(Enchantment.MULTISHOT, 1, true);

            m2.setItemMeta(m2Meta);
            return m2;
        });

        // /give @p repeating_command_block{Unbreakable:1,display:{Name:'[{"text":"Season 2 Medallion","italic":false,"color":"#7e2e88"}]',Lore:['[{"text":"This special token recognizes","italic":false,"color":"#d14081"}]','[{"text":"your long-standing dedication","italic":false,"color":"#d14081"}]','[{"text":"to the Dumb Dog Diner","italic":false,"color":"#d14081"}]','[{"text":"Minecraft server network.","italic":false,"color":"#d14081"}]','[{"text":"","italic":false,"color":"#d14081"}]','[{"text":"Live together, shop","italic":false,"color":"#d14081"}]','[{"text":"together, fight together!","italic":false,"color":"#d14081"},{"text":"","italic":false,"color":"dark_purple"}]','[{"text":"","italic":false,"color":"dark_purple"}]','[{"text":"- Stixil","italic":false,"color":"#ff798a"}]']},Enchantments:[{id:multishot,lvl:1}]} 1

        medallionList.add(name -> {
            ItemStack m3 = new ItemStack(Material.JIGSAW);
            ItemMeta m3Meta = m3.getItemMeta();
            m3Meta.setUnbreakable(true);
            m3Meta.displayName(Component.text("Season 3 Medallion").color(TextColor.fromCSSHexString("#16697a")));

            TextColor m3LoreColor = TextColor.fromCSSHexString("#82c0cc");
            List<Component> m3Lore = new ArrayList<>();

            m3Lore.add(Component.text("To " + name + ":").color(m3LoreColor));
            m3Lore.add(Component.text(""));
            m3Lore.add(Component.text(""));
            m3Lore.add(Component.text("This special token").color(m3LoreColor));
            m3Lore.add(Component.text("recognizes your dedication").color(m3LoreColor));
            m3Lore.add(Component.text("to the Dumb Dog Diner").color(m3LoreColor));
            m3Lore.add(Component.text("Minecraft server network").color(m3LoreColor));
            m3Lore.add(Component.text(""));
            m3Lore.add(Component.text(""));
            m3Lore.add(Component.text("Starlight").color(TextColor.fromCSSHexString("#ffff99"))
                .append(Component.text(" and ").color(m3LoreColor)
                    .append(Component.text("Sunrise").color(TextColor.fromCSSHexString("#ff6666")))));
            m3Lore.add(Component.text("will never forget you!").color(m3LoreColor));
            m3Lore.add(Component.text(""));
            m3Lore.add(Component.text(""));
            m3Lore.add(Component.text("- Stixil").color(TextColor.fromCSSHexString("#ccffff")).decoration(TextDecoration.ITALIC, true));

            m3Meta.lore(m3Lore);
            m3Meta.addEnchant(Enchantment.MENDING, 1, true);

            m3.setItemMeta(m3Meta);
            return m3;
        });
        // /give @p jigsaw{Unbreakable:1,display:{Name:'[{"text":"Season 3 Medallion","italic":false,"color":"#16697a"}]',Lore:['[{"text":"This special token","italic":false,"color":"#82c0cc"}]','[{"text":"recognizes your dedication","italic":false,"color":"#82c0cc"}]','[{"text":"to the Dumb Dog Diner","italic":false,"color":"#82c0cc"}]','[{"text":"Minecraft server network.","italic":false,"color":"#82c0cc"},{"text":"","italic":false,"color":"dark_purple"}]','[{"text":"","italic":false,"color":"dark_purple"}]','[{"text":"Starlight ","italic":false,"color":"#ffff99"},{"text":"and ","color":"#82c0cc"},{"text":"Sunrise","color":"#ff6666"},{"text":"","color":"dark_purple"}]','[{"text":"will never forget you!","italic":false,"color":"#82c0cc"},{"text":"","italic":false,"color":"dark_purple"}]','[{"text":"","italic":false,"color":"dark_purple"}]','[{"text":"- Stixil","italic":false,"color":"#ccffff"}]']},Enchantments:[{id:mending,lvl:1}]} 1


        return medallionList;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event){
        // todo: fix user class!
        Player p = event.getPlayer();
        User u = User.fromPlayer(p);
        if(!u.isFirstJoinItemsGiven()) {
            List<Integer> seasons = medallionsList.getOrDefault(u.getUniqueId(), new ArrayList<>());
            if((Arrays.stream(p.getInventory().getContents()).filter((Predicate<ItemStack>) Objects::isNull).count() <= seasons.size())) {
                Map<String, String> vars = pluginInstance.getLocaleProvider().newVariables();
                vars.put("SLOTS", Integer.toString(seasons.size()));
                p.sendMessage(pluginInstance.getLocaleProvider().translate("medallions.no-space", vars));
            }
            u.setFirstJoinItemsGiven(true);
             for(int i : seasons){
                p.getInventory().addItem(medallions.get(i).apply(p.getName()));
             }
        }
    }
}
