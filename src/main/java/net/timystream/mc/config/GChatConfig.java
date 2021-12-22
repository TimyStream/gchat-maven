package net.timystream.mc.config;

import com.google.common.collect.ImmutableList;

import net.timystream.mc.api.ChatFormat;
import net.timystream.mc.GChatPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GChatConfig {
    private static final Style DEFAULT_LINK_STYLE = Style.style(NamedTextColor.WHITE, TextDecoration.UNDERLINED);
    private final boolean passthrough;
    private final boolean requireSendPermission;
    private final Component requireSendPermissionFailMessage;
    private final boolean requireReceivePermission;
    private final boolean requirePermissionPassthrough;
    private final boolean logChatGlobal;
    private final List<ChatFormat> formats;
    private final Style linkStyle;

    public GChatConfig(ConfigurationNode c) {
        this.passthrough = c.getNode("passthrough").getBoolean(true);

        this.logChatGlobal = c.getNode("log-chat-global").getBoolean(true);

        ConfigurationNode requirePermission = c.getNode("require-permission");
        if (requirePermission.isVirtual()) {
            throw new IllegalArgumentException("Missing section: require-permission");
        }

        this.requireSendPermission = requirePermission.getNode("send").getBoolean(false);

        String failMsg = getStringNonNull(requirePermission, "send-fail");
        if (failMsg.isEmpty()) {
            requireSendPermissionFailMessage = null;
        } else {
            requireSendPermissionFailMessage = GChatPlugin.LEGACY_LINKING_SERIALIZER.deserialize(failMsg);
        }

        this.requireReceivePermission = requirePermission.getNode("receive").getBoolean(false);
        this.requirePermissionPassthrough = requirePermission.getNode("passthrough").getBoolean(true);

        ConfigurationNode formatsSection = c.getNode("formats");
        if (formatsSection.isVirtual()) {
            throw new IllegalArgumentException("Missing section: formats");
        }

        Map<String, ChatFormat> currentFormats = new HashMap<>();
        for (Object id : formatsSection.getChildrenMap().keySet()) {
            ConfigurationNode formatSection = formatsSection.getNode(id);
            if (formatSection.isVirtual() || !(id instanceof String)) {
                continue;
            }

            String key = (String) id;

            currentFormats.put(key.toLowerCase(), new ChatFormat(key.toLowerCase(), formatSection));
        }

        List<ChatFormat> formatsList = new ArrayList<>(currentFormats.values());
        formatsList.sort(Comparator.comparingInt(ChatFormat::getPriority));

        this.formats = ImmutableList.copyOf(formatsList);

        Style currentLinkStyle;
        try {
            //noinspection UnstableApiUsage
            currentLinkStyle = c.getNode("link-style").getValue(TypeTokens.STYLE, DEFAULT_LINK_STYLE);
        } catch (ObjectMappingException e) {
            currentLinkStyle = DEFAULT_LINK_STYLE;
        }

        this.linkStyle = currentLinkStyle;
    }

    public static String getStringNonNull(ConfigurationNode configuration, String path) throws IllegalArgumentException {
        String ret = configuration.getNode(path).getString();
        if (ret == null) {
            throw new IllegalArgumentException("Missing string value at '" + path + "'");
        }

        return ret;
    }

    public boolean isPassthrough() {
        return this.passthrough;
    }

    public boolean isRequireSendPermission() {
        return this.requireSendPermission;
    }

    public Component getRequireSendPermissionFailMessage() {
        return this.requireSendPermissionFailMessage;
    }

    public boolean isRequireReceivePermission() {
        return this.requireReceivePermission;
    }

    public boolean isRequirePermissionPassthrough() {
        return this.requirePermissionPassthrough;
    }

    public boolean isLogChatGlobal() {
        return this.logChatGlobal;
    }

    public List<ChatFormat> getFormats() {
        return this.formats;
    }

    public Style getLinkStyle() {
        return this.linkStyle;
    }

    public String toString() {
        return "GChatConfig(passthrough=" + this.isPassthrough() + ", requireSendPermission=" + this.isRequireSendPermission() + ", requireSendPermissionFailMessage=" + this.getRequireSendPermissionFailMessage() + ", requireReceivePermission=" + this.isRequireReceivePermission() + ", requirePermissionPassthrough=" + this.isRequirePermissionPassthrough() + ", logChatGlobal=" + this.isLogChatGlobal() + ", formats=" + this.getFormats() + ", linkStyle=" + this.getLinkStyle() + ")";
    }
}
