package com.dumbdogdiner.stickycommands.managers;

import com.dumbdogdiner.stickycommands.utils.PowerTool;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class PowerToolManager {
    @Getter
    private static final HashSet<PowerTool> powerTools = new HashSet<>();

    @Nullable
    public static PowerTool getPowerTool(@NotNull Player player, @NotNull Material type) {
        return powerTools.stream()
                .filter(powerTool -> powerTool.getPlayer() == player && powerTool.getItem() == type)
                .findFirst()
                .orElse(null);
    }

    public static void add(PowerTool powerTool) {
        powerTools.add(powerTool);
    }

    public static void remove(PowerTool powerTool) {
        powerTools.remove(powerTool);
    }

    public static void remove(@NotNull Player player) {
        powerTools.removeIf(powerTool -> powerTool.getPlayer() == player);
    }
}
