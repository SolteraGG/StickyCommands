package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.common.translation.LocaleProvider;
import com.dumbdogdiner.stickycommands.StickyCommands;
import com.dumbdogdiner.stickycommands.managers.MedallionManager;
import com.dumbdogdiner.stickycommands.utils.Constants;
import com.dumbdogdiner.stickycommands.utils.ItemWorths;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

@Command("stickycommands")
//@Permission(Constants.Permissions.STICKYCOMMANDS)
public class MainCommand {
    private static final LocaleProvider locale = StickyCommands.getInstance().getLocaleProvider();
    @Default
    public static void info(CommandSender sender){
        var vars = LocaleProvider.newVariables();
        PluginDescriptionFile pdf = StickyCommands.getInstance().getDescription();

        vars.put("plugin_name", pdf.getName());
        vars.put("plugin_version", pdf.getVersion());
        vars.put("authors", String.join(", ", pdf.getAuthors()));
        sender.sendMessage(locale.translate(Constants.Messages.STICKYCOMMANDS_VERSION, vars));
    }

    @Subcommand("reload")
    public static void reload(CommandSender sender){
        ItemWorths.reloadWorths();
        StickyCommands.getInstance().reloadConfig();
        StickyCommands.getInstance().getLocaleProvider().loadAllLocales();
        RulebookCommand.reloadRulebook();
        MedallionManager.reloadMedallions();
    }

}
