package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.bukkit.command.StickyPluginCommand;
import com.dumbdogdiner.stickyapi.bukkit.item.generator.BookGenerator;
import com.dumbdogdiner.stickyapi.bukkit.plugin.StickyPlugin;
import com.dumbdogdiner.stickyapi.bukkit.util.ServerUtil;
import com.dumbdogdiner.stickyapi.common.arguments.Arguments;
import com.dumbdogdiner.stickyapi.common.book.chat.JsonComponent;
import com.dumbdogdiner.stickyapi.common.book.commonmarkextensions.JsonComponentWriter;
import com.dumbdogdiner.stickyapi.common.book.commonmarkextensions.MCFormatExtension;
import com.dumbdogdiner.stickyapi.common.book.commonmarkextensions.MarkdownJsonRenderer;
import com.dumbdogdiner.stickyapi.common.command.ExitCode;
import com.dumbdogdiner.stickyapi.common.translation.Translation;
import com.dumbdogdiner.stickyapi.common.util.BookUtil;
import com.dumbdogdiner.stickycommands.StickyCommands;
import org.bukkit.Material;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.commonmark.node.Document;
import org.commonmark.parser.Parser;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RulesCommand extends StickyPluginCommand {
    public static final String PERMISSION = "stickycommands.rules";
    public static final String RULEBOOK_LOCAL_FILENAME = "rulebook.md";
    public static final String RULEBOOK_URL_CONFIG_PATH = "rulebook-url";

    public RulesCommand(@NotNull StickyPlugin owner) {
        super(
                /* name:       */ "rules",
                /* aliases:    */ Collections.singletonList("rulebook"),
                /* owner:      */ owner,
                /* permission: */ new Permission(PERMISSION)
        );
        // settings not settable in super call
        requiresPlayer = true;
    }

    private static Reader getRulebookReader() {
        var plugin = StickyCommands.getInstance();
        try {
            var configUrl = plugin.getConfig().getString(RULEBOOK_URL_CONFIG_PATH);
            if (configUrl != null) {
                var url = new URL(configUrl);
                return new InputStreamReader(url.openStream());
            }
            var dataFolder = plugin.getDataFolder();
            var rulebook = new File(dataFolder, "rulebook.md");
            if (!rulebook.exists()) {
                plugin.saveResource(RULEBOOK_LOCAL_FILENAME, true);
                return new FileReader(rulebook);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        var resource = plugin.getResource(RULEBOOK_LOCAL_FILENAME);
        if (resource == null) {
            return new StringReader("The rulebook could not be loaded.");
        } else {
            return new InputStreamReader(resource);
        }
    }

    private static ItemStack generateDefault() throws IOException {
        var config = StickyCommands.getInstance().getConfig();
        var title = config.getString("rulebook-title");
        if (title == null) title = "&ddddMC Survival Handbook";
        var author = config.getString("rulebook-author");
        if (author == null) author = "Stixil";
        try (var reader = getRulebookReader()) {
            return generate(
                    reader,
                    Translation.translateColors("&", title),
                    Translation.translateColors("&", author)
            );
        }
    }

    private static ItemStack generate(Reader reader, String title, String author) throws IOException {
        var builder = Parser.builder();
        ((MCFormatExtension) MCFormatExtension.create()).extend(builder);
        var cmparser = builder.build();
        var bookGenerator = new BookGenerator(Material.WRITTEN_BOOK);
        var sections = BookUtil.splitDocumentByBreaks((Document) cmparser.parseReader(reader));
        sections.stream()
                .map(section -> BookUtil.splitBookPages(renderDocument(section)))
                .flatMap(List::stream)
                .forEach(page -> bookGenerator.addPage(page.toJson()));
        return bookGenerator.setTitle(title).setAuthor(author).toItemStack(1);
    }

    private static JsonComponent renderDocument(Document document) {
        var component = new JsonComponent();
        var writer = new JsonComponentWriter(component);
        new MarkdownJsonRenderer(writer).render(document);
        return component;
    }

    @Override
    public ExitCode execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull Arguments arguments, @NotNull Map<String, String> map) {
        try {
            ((Player) commandSender).getInventory().addItem(generateDefault());
        } catch (IOException e) {
            e.printStackTrace();
            return ExitCode.EXIT_ERROR;
        }

        return ExitCode.EXIT_SUCCESS;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) throws IllegalArgumentException, CommandException {
        return Collections.emptyList();
    }
}
