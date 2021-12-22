package net.timystream.mc.api.events;

import com.google.common.base.Objects;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;

import net.timystream.mc.api.ChatFormat;

public class GChatMessageSendEvent implements ResultedEvent<PlayerChatEvent.ChatResult> {
    
    private final Player sender;
    private final Player recipient;
    private final ChatFormat format;
    private final String rawMessage;

    private PlayerChatEvent.ChatResult result;

    public GChatMessageSendEvent(Player sender, Player recipient, ChatFormat format, String rawMessage, boolean cancelled) {
        this.sender = sender;
        this.recipient = recipient;
        this.format = format;
        this.rawMessage = rawMessage;
        this.result = cancelled ? PlayerChatEvent.ChatResult.denied() : PlayerChatEvent.ChatResult.allowed();
    }

    public Player getSender() {
        return this.sender;
    }

    public Player getRecipient() {
        return this.recipient;
    }

    public ChatFormat getFormat() {
        return this.format;
    }

    public String getRawMessage() {
        return this.rawMessage;
    }

    public PlayerChatEvent.ChatResult getResult() {
        return this.result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GChatMessageSendEvent that = (GChatMessageSendEvent) o;
        return Objects.equal(sender, that.sender) && Objects.equal(recipient, that.recipient) && Objects.equal(format, that.format) && Objects.equal(rawMessage, that.rawMessage) && Objects.equal(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sender, recipient, format, rawMessage, result);
    }

    public String toString() {
        return "GChatMessageSendEvent(sender=" + this.getSender() + ", recipient=" + this.getRecipient() + ", format=" + this.getFormat() + ", rawMessage=" + this.getRawMessage() + ", result=" + this.getResult() + ")";
    }

    public void setResult(PlayerChatEvent.ChatResult result) {
        this.result = result;
    }
}
