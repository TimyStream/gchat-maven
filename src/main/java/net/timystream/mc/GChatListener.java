package net.timystream.mc;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;

import net.timystream.mc.api.ChatFormat;
import net.timystream.mc.api.events.GChatEvent;
import net.timystream.mc.api.events.GChatMessageFormedEvent;
import net.timystream.mc.api.events.GChatMessageSendEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.regex.Pattern;

public class GChatListener {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)([§&])[0-9A-FK-OR]");

    private final GChatPlugin plugin;
    private final LegacyComponentSerializer legacyLinkingSerializer;

    public GChatListener(GChatPlugin plugin) {
        this.plugin = plugin;
        this.legacyLinkingSerializer = LegacyComponentSerializer.builder()
                .character('&')
                .extractUrls(plugin.getConfig().getLinkStyle())
                .build();

    }

    @Subscribe(order = PostOrder.NORMAL)
    public void onChat(PlayerChatEvent e) {
        Player player = e.getPlayer();

        GChatEvent gChatEvent = new GChatEvent(player, e);
        plugin.getProxy().getEventManager().fire(gChatEvent).join();

        if (!gChatEvent.getResult().isAllowed()) {
            return;
        }

        // are permissions required to send chat messages?
        // does the player have perms to send the message
        if (plugin.getConfig().isRequireSendPermission() && !player.hasPermission("gchat.send")) {

            // if the message should be passed through when the player doesn't have the permission
            if (plugin.getConfig().isRequirePermissionPassthrough()) {
                // just return. the default behaviour is for the message to be passed to the backend.
                return;
            }

            // they don't have permission, and the message shouldn't be passed to the backend.
            e.setResult(PlayerChatEvent.ChatResult.denied());

            Component failMessage = plugin.getConfig().getRequireSendPermissionFailMessage();
            if (failMessage != null) {
                player.sendMessage(failMessage);
            }

            return;
        }

        ChatFormat format = plugin.getFormat(player).orElse(null);

        // couldn't find a format for the player
        if (format == null) {
            if (!plugin.getConfig().isPassthrough()) {
                e.setResult(PlayerChatEvent.ChatResult.denied());
            }

            return;
        }

        // we have a format, so cancel the event.
        e.setResult(PlayerChatEvent.ChatResult.denied());

        // get the actual message format, and apply replacements.
        String formatText = format.getFormatText();
        formatText = plugin.replacePlaceholders(player, formatText);

        // get any hover text, and apply replacements.
        String hover = format.getHoverText();
        hover = plugin.replacePlaceholders(player, hover);

        // get the click event type, and the value if present.
        ClickEvent.Action clickType = format.getClickType();
        String clickValue = format.getClickValue();
        if (clickType != null) {
            clickValue = plugin.replacePlaceholders(player, clickValue);
        }

        // get the players message, and remove any color if they don't have permission for it.
        String playerMessage = e.getMessage();
        if (!player.hasPermission("gchat.color")) {
            playerMessage = STRIP_COLOR_PATTERN.matcher(playerMessage).replaceAll("");
        }

        // apply the players message to the chat format
        formatText = formatText.replace("{message}", playerMessage);

        // apply any hover events
        HoverEvent<Component> hoverEvent = hover == null ? null : HoverEvent.showText(LegacyComponentSerializer.legacyAmpersand().deserialize(hover));
        ClickEvent clickEvent = clickType == null ? null : ClickEvent.clickEvent(clickType, clickValue);

        // convert the format to a message
        TextComponent message = legacyLinkingSerializer
                .deserialize(formatText)
                .toBuilder()
                .applyDeep(m -> {
                    Component mComponent = m.build();

                    if (hoverEvent != null && mComponent.hoverEvent() == null) {
                        m.hoverEvent(hoverEvent);
                    }
                    if (clickEvent != null && mComponent.clickEvent() == null) {
                        m.clickEvent(clickEvent);
                    }
                })
                .build();

        GChatMessageFormedEvent formedEvent = new GChatMessageFormedEvent(player, format, playerMessage, message);
        plugin.getProxy().getEventManager().fireAndForget(formedEvent);

        if (plugin.getConfig().isLogChatGlobal()) {
            plugin.getLogger().info(PlainTextComponentSerializer.plainText().serialize(message));
        }

        // send the message to online players
        for (Player p : plugin.getProxy().getAllPlayers()) {
            boolean cancelled = plugin.getConfig().isRequireReceivePermission() && !player.hasPermission("gchat.receive");
            GChatMessageSendEvent sendEvent = new GChatMessageSendEvent(player, p, format, playerMessage, cancelled);
            plugin.getProxy().getEventManager().fire(sendEvent).join();

            if (!sendEvent.getResult().isAllowed()) {
                continue;
            }

            p.sendMessage(player, message);
        }
    }

}
