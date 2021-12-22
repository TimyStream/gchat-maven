package net.timystream.mc.api;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.event.ClickEvent;
import net.timystream.mc.config.GChatConfig;
import ninja.leaping.configurate.ConfigurationNode;

public class ChatFormat {
    
    private final String id;
    private final int priority;
    private final boolean checkPermission;
    private final String formatText;
    private final String hoverText;
    private final ClickEvent.Action clickType;
    private final String clickValue;

    public ChatFormat(String id, ConfigurationNode c) {
        this.id = id;
        this.priority = c.getNode("priority").getInt(0);
        this.checkPermission = c.getNode("check-permission").getBoolean(true);
        this.formatText = GChatConfig.getStringNonNull(c, "format");

        String currentHoverText = null;
        ClickEvent.Action currentClickType = null;
        String currentClickValue = null;

        ConfigurationNode extra = c.getNode("format-extra");
        if (!extra.isVirtual()) {
            String hover = extra.getNode("hover").getString();
            if (hover != null && !hover.isEmpty()) {
                currentHoverText = hover;
            }

            ConfigurationNode click = extra.getNode("click");
            if (!click.isVirtual()) {
                String type = click.getNode("type").getString("none").toLowerCase();
                String value = click.getNode("value").getString();

                if (!type.equals("none") && value != null) {
                    if (!type.equals("suggest_command") && !type.equals("run_command") && !type.equals("open_url")) {
                        throw new IllegalArgumentException("Invalid click type: " + type);
                    }

                    currentClickType = ClickEvent.Action.valueOf(type.toUpperCase());
                    currentClickValue = value;
                }
            }
        }

        this.hoverText = currentHoverText;
        this.clickType = currentClickType;
        this.clickValue = currentClickValue;
    }

    public ChatFormat(String id, int priority, boolean checkPermission, String formatText, String hoverText, ClickEvent.Action clickType, String clickValue) {
        this.id = id;
        this.priority = priority;
        this.checkPermission = checkPermission;
        this.formatText = formatText;
        this.hoverText = hoverText;
        this.clickType = clickType;
        this.clickValue = clickValue;
    }

    public boolean canUse(Player player) {
        return !checkPermission || player.hasPermission("gchat.format." + id);
    }

    public String getId() {
        return this.id;
    }

    public int getPriority() {
        return this.priority;
    }

    public boolean isCheckPermission() {
        return this.checkPermission;
    }

    public String getFormatText() {
        return this.formatText;
    }

    public String getHoverText() {
        return this.hoverText;
    }

    public ClickEvent.Action getClickType() {
        return this.clickType;
    }

    public String getClickValue() {
        return this.clickValue;
    }

    public String toString() {
        return "ChatFormat(id=" + this.getId() + ", priority=" + this.getPriority() + ", checkPermission=" + this.isCheckPermission() + ", formatText=" + this.getFormatText() + ", hoverText=" + this.getHoverText() + ", clickType=" + this.getClickType() + ", clickValue=" + this.getClickValue() + ")";
    }
}
