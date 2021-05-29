package com.dumbdogdiner.stickycommands.utils;

import com.dumbdogdiner.stickycommands.StickyCommands;
import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class ResourceUtils {
    private static final StickyCommands instance = StickyCommands.getInstance();
    public File getOrCreate(String resourcePath){
        File file = new File(instance.getDataFolder(), resourcePath);
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            instance.saveResource(resourcePath, false);
        }
        return file;
    }
}
