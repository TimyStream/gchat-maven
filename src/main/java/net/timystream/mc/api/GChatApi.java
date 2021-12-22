package net.timystream.mc.api;

import com.velocitypowered.api.proxy.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GChatApi {
    
    /**
     * Registers a placeholder with gChat
     *
     * @param placeholder the placeholder
     * @return true if the placeholder wasn't already registered
     */
    boolean registerPlaceholder(Placeholder placeholder);

    /**
     * Unregisters a placeholder with gChat
     *
     * @param placeholder the placeholder
     * @return true if the placeholder was previously registered
     */
    boolean unregisterPlaceholder(Placeholder placeholder);

    /**
     * Gets an immutable set of the placeholders registered
     *
     * @return a set of placeholders
     */
    Set<Placeholder> getPlaceholders();

    /**
     * Gets an immutable list of formats registered
     *
     * <p>The list is sorted in order of {@link ChatFormat#getPriority()}, with the highest priority
     * format coming first in the list.</p>
     *
     * @return a list of formats
     */
    List<ChatFormat> getFormats();

    /**
     * Performs a placeholder replacement on the given message
     *
     * @param player the player to replace in the context of
     * @param text   the text containing the placeholders to be replaced
     * @return the replaced text
     */
    String replacePlaceholders(Player player, String text);

    /**
     * Gets the most applicable chat format for a given player
     *
     * @param player the player
     * @return a chat format for the player, if any
     */
    Optional<ChatFormat> getFormat(Player player);

    /**
     * Reloads the plugin from the config file
     *
     * @return true if the operation was successful
     */
    boolean reloadConfig();

}
