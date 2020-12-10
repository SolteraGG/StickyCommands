package com.dumbdogdiner.stickycommands.commands;

import com.dumbdogdiner.stickyapi.bukkit.command.builder.CommandBuilder;
import com.dumbdogdiner.stickyapi.bukkit.item.generator.BookGenerator;
import com.dumbdogdiner.stickyapi.common.book.chat.JsonComponent;
import com.dumbdogdiner.stickyapi.common.book.commonmarkextensions.JsonComponentWriter;
import com.dumbdogdiner.stickyapi.common.book.commonmarkextensions.MCFormatExtension;
import com.dumbdogdiner.stickyapi.common.book.commonmarkextensions.MarkdownJsonRenderer;
import com.dumbdogdiner.stickyapi.common.command.ExitCode;
import com.dumbdogdiner.stickyapi.common.util.BookUtil;
import com.dumbdogdiner.stickycommands.StickyCommands;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.commonmark.node.Document;
import org.commonmark.parser.Parser;

import java.io.*;
import java.util.Collections;
import java.util.List;

public class RulesCommand {
    public static final String PERMISSION = "stickycommands.rules";

    public static void build(Plugin owner) {
        new CommandBuilder("rules")
                .description("Get a copy of the the server's rules")
                .permission(PERMISSION)
                .alias("rulebook")
                .requiresPlayer(true)
                .onTabComplete((sender, s, arguments) -> Collections.emptyList())
                .onExecute((sender, arguments, vars) -> {
                    if (!sender.hasPermission(PERMISSION)) {
                        return ExitCode.EXIT_PERMISSION_DENIED;
                    }

                    if (!(sender instanceof Player)) {
                        return ExitCode.EXIT_MUST_BE_PLAYER;
                    }

                    try {
                        ((Player) sender).getInventory().addItem(generateDefault());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return ExitCode.EXIT_ERROR;
                    }

                    return ExitCode.EXIT_SUCCESS;
                })
                .onError((exitCode, sender, arguments, vars) -> {
                    var provider = StickyCommands.getInstance().getLocaleProvider();
                    switch (exitCode) {
                        case EXIT_PERMISSION_DENIED:
                            sender.sendMessage(provider.translate("no-permission", vars));
                            break;
                        case EXIT_MUST_BE_PLAYER:
                            sender.sendMessage(provider.translate("must-be-player", vars));
                            break;
                        case EXIT_ERROR:
                            sender.sendMessage(provider.translate("server-error", vars));
                            break;
                        case EXIT_SUCCESS:
                            break;
                        default:
                            sender.sendMessage(ChatColor.RED + "Exited with " + exitCode);
                    }
                })
                .register(owner);
    }

    private static Reader getRulebookReader() {
        StickyCommands plugin = StickyCommands.getInstance();
        try {
            var dataFolder = plugin.getDataFolder();
            var rulebook = new File(dataFolder, "rulebook.md");
            if (!rulebook.exists()) {
                if (!dataFolder.mkdirs() || !rulebook.createNewFile()) {
                    throw new IOException("Could not save default rulebook to data folder");
                }
                try (var writer = new FileOutputStream(rulebook)) {
                    try (var defaultRulebook = plugin.getResource("rulebook.md")) {
                        if (defaultRulebook == null) throw new IllegalStateException("No rulebook in the resources!");
                        byte[] bytes;
                        do {
                            bytes = defaultRulebook.readNBytes(2048);
                            writer.write(bytes);
                        } while (bytes.length > 0);
                        writer.write(defaultRulebook.read());
                    }
                }
                return new FileReader(rulebook);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        var resource = plugin.getResource("rulebook.md");
        if (resource == null) {
            return new StringReader("The rulebook could not be loaded.");
        } else {
            return new InputStreamReader(resource);
        }
    }

    private static ItemStack generateDefault() throws IOException {
        try (var reader = getRulebookReader()) {
            return generate(reader, "§ddddMC Survival Handbook", "Stixil");
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
}
