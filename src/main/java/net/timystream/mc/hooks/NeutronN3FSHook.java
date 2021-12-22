package net.timystream.mc.hooks;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;

import me.crypnotic.neutron.NeutronPlugin;
import me.crypnotic.neutron.api.Neutron;

import net.timystream.mc.api.events.GChatMessageSendEvent;

public class NeutronN3FSHook {
    
    private final NeutronPlugin neutron;

    public NeutronN3FSHook() {
        neutron = Neutron.getNeutron();
    }

    @Subscribe(order = PostOrder.LATE)
    public void onMessageSend(GChatMessageSendEvent e) {
        neutron.getUserManager()
                .getUser(e.getRecipient())
                .ifPresent(user -> {
                    if (user.isIgnoringPlayer(e.getSender())) {
                        e.setResult(PlayerChatEvent.ChatResult.denied());
                    }
                });
    }
}
