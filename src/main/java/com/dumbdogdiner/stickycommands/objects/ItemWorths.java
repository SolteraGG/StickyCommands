package com.dumbdogdiner.stickycommands.objects;

import com.dumbdogdiner.stickycommands.StickyCommands;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class ItemWorths extends HashMap<Material, Double> {
    public ItemWorths(StickyCommands instance){
        // TODO: move the following to some common way
        instance.getDataFolder();
    }
}
