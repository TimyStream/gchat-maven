package net.timystream.mc.hooks;

import net.timystream.mc.api.Placeholder;

import com.velocitypowered.api.proxy.Player;

import me.lucko.luckperms.placeholders.LPPlaceholderProvider;
import me.lucko.luckperms.placeholders.PlaceholderPlatform;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

public class LuckPermsHook implements Placeholder, PlaceholderPlatform {
    private final LuckPerms luckPerms;
    private final LPPlaceholderProvider provider;

    public LuckPermsHook() {
        this.luckPerms = LuckPermsProvider.get();
        this.provider = new LPPlaceholderProvider(this, this.luckPerms);
    }

    @Override
    public String getReplacement(Player player, String identifier) {
        if (identifier.startsWith("lp_")) {
            identifier = identifier.substring("lp_".length());
        } else if (identifier.startsWith("luckperms_")) {
            identifier = identifier.substring("luckperms_".length());
        } else {
            return null;
        }

        if (player == null || this.provider == null) {
            return "";
        }

        return this.provider.onPlaceholderRequest(player, player.getUniqueId(), identifier);
    }

    @Override
    public String formatBoolean(boolean b) {
        return b ? "yes" : "no";
    }

}
