package net.timystream.mc.api.events;

import com.google.common.base.Objects;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;

public class GChatEvent implements ResultedEvent<PlayerChatEvent.ChatResult> {
    
    private final Player sender;
    private final PlayerChatEvent chatEvent;

    private PlayerChatEvent.ChatResult result;

    public GChatEvent(Player sender, PlayerChatEvent chatEvent) {
        this.sender = sender;
        this.chatEvent = chatEvent;
        this.result = chatEvent.getResult();
    }

    public Player getSender() {
        return this.sender;
    }

    public PlayerChatEvent getChatEvent() {
        return this.chatEvent;
    }

    public PlayerChatEvent.ChatResult getResult() {
        return this.result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GChatEvent that = (GChatEvent) o;
        return Objects.equal(sender, that.sender) && Objects.equal(chatEvent, that.chatEvent) && Objects.equal(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sender, chatEvent, result);
    }

    public String toString() {
        return "GChatEvent(sender=" + this.getSender() + ", chatEvent=" + this.getChatEvent() + ", result=" + this.getResult() + ")";
    }

    public void setResult(PlayerChatEvent.ChatResult result) {
        this.result = result;
    }

}
