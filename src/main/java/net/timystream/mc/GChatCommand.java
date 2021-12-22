package net.timystream.mc;

import com.velocitypowered.api.command.CommandSource;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Collections;
import java.util.List;

public class GChatCommand implements SimpleCommand {
    private static final TextComponent PREFIX = Component.text("[").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, true)
    .append(Component.text("gChat").color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true))
    .append(Component.text("]").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, true))
    .append(Component.text(" ").decoration(TextDecoration.BOLD, false));

    private final GChatPlugin plugin;

    public GChatCommand(GChatPlugin plugin) {
    this.plugin = plugin;
    }

    @Override
    public void execute(SimpleCommand.Invocation invocation) {
    CommandSource source = invocation.source();
    String[] args = invocation.arguments();

    if (args.length == 0) {
        TextComponent versionMsg = PREFIX.append(Component.text("Running gChat for Velocity ").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, false))
                .append(Component.text("v" + plugin.getDescription().getVersion().get()).color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false))
                .append(Component.text(".").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, false));

        source.sendMessage(versionMsg);
        return;
    }

    String subCommand = args[0].toLowerCase();

    if (subCommand.equals("reload") && source.hasPermission("gchat.command.reload")) {
        boolean result = plugin.reloadConfig();

        TextComponent reloadMsg;
        if (result) {
            reloadMsg = PREFIX.append(Component.text("Reload successful.").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, false));
        } else {
            reloadMsg = PREFIX.append(Component.text("Reload failed. Check the console for errors").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, false));
        }

        source.sendMessage(reloadMsg);
        return;
    }

    TextComponent unknownCommand = PREFIX.append(Component.text("Unknown sub command.").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false));
    source.sendMessage(unknownCommand);
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return Collections.emptyList();
}
}
