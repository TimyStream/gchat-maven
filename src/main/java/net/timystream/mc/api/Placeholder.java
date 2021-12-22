package net.timystream.mc.api;

import com.velocitypowered.api.proxy.Player;

public interface Placeholder {
    /**
     * Gets a replacement for a given placeholder.
     *
     * @param player     the associated player
     * @param definition the placeholder definition, without the outer "{ }" brackets.
     * @return a replacement, or null if the definition cannot be satisfied by this {@link Placeholder}
     */
    String getReplacement(Player player, String definition);

}
