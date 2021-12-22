package net.timystream.mc.placeholder;

import com.velocitypowered.api.proxy.Player;
import net.timystream.mc.api.Placeholder;

public class StandardPlaceholders implements Placeholder {
    
    @Override
    public String getReplacement(Player player, String definition) {

        // dynamic placeholders
        if (definition.startsWith("has_perm_") && definition.length() > "has_perm_".length()) {
            String perm = definition.substring("has_perm_".length());
            return Boolean.toString(player.hasPermission(perm));
        }

        // static placeholders
        switch (definition.toLowerCase()) {
            case "username":
            case "name":
            case "display_username": // Velocity doesn't have display names
            case "display_name":
                return player.getUsername();
            case "server_name":
                return player.getCurrentServer().get().getServerInfo().getName();
            case "uuid":
                return player.getUniqueId().toString();
            default:
                return null;
        }
    }
}
