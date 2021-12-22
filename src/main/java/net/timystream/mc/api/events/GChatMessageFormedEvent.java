package net.timystream.mc.api.events;

import com.google.common.base.Objects;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import net.timystream.mc.api.ChatFormat;

public class GChatMessageFormedEvent {
    
    private final Player sender;
    private final ChatFormat format;
    private final String rawMessage;
    private final Component message;

    public GChatMessageFormedEvent(Player sender, ChatFormat format, String rawMessage, Component message) {
        this.sender = sender;
        this.format = format;
        this.rawMessage = rawMessage;
        this.message = message;
    }

    public Player getSender() {
        return this.sender;
    }

    public ChatFormat getFormat() {
        return this.format;
    }

    public String getRawMessage() {
        return this.rawMessage;
    }

    public Component getMessage() {
        return this.message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GChatMessageFormedEvent that = (GChatMessageFormedEvent) o;
        return Objects.equal(sender, that.sender) && Objects.equal(format, that.format) && Objects.equal(rawMessage, that.rawMessage) && Objects.equal(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sender, format, rawMessage, message);
    }

    public String toString() {
        return "GChatMessageFormedEvent(sender=" + this.getSender() + ", format=" + this.getFormat() + ", rawMessage=" + this.getRawMessage() + ", message=" + this.getMessage() + ")";
    }
}
