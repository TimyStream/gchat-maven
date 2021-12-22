package net.timystream.mc;

import net.timystream.mc.api.GChatApi;
import net.timystream.mc.api.ChatFormat;
import net.timystream.mc.api.Placeholder;
import net.timystream.mc.config.GChatConfig;
import net.timystream.mc.hooks.LuckPermsHook;
import net.timystream.mc.hooks.NeutronN3FSHook;
import net.timystream.mc.placeholder.StandardPlaceholders;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.kyori.adventure.serializer.configurate3.ConfigurateComponentSerializer;
//import net.kyori.adventure.serializer.configurate3.ConfigurateComponentSerializer;
//import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;


@Plugin(
        id = "gchat-velocity",
        name = "gchat-velocity",
        description = "A simple global chat plugin for Velocity.",
        authors = {"Luck", "md678685", "Timy Stream"},
        version = "1.0.3-SNAPSHOT",
        dependencies = {
                @Dependency(id = "luckperms", optional = true),
                @Dependency(id = "neutron-n3fs", optional = true)
        }
)
public class GChatPlugin implements GChatApi{
    
    public static final LegacyComponentSerializer LEGACY_LINKING_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .extractUrls()
            .build();
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^\\{\\}]+)\\}");

    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;
    private final Set<Placeholder> placeholders = ConcurrentHashMap.newKeySet();

    private GChatConfig config;

    @Inject
    public GChatPlugin(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        logger.info("Enabling gChat v" + getDescription().getVersion().orElse("Unknown"));

        // load configuration
        try {
            this.config = loadConfig();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }

        // init placeholder hooks
        placeholders.add(new StandardPlaceholders());

        // hook with luckperms
        if (proxy.getPluginManager().getPlugin("luckperms").isPresent()) {
            placeholders.add(new LuckPermsHook());
        }

        if (proxy.getPluginManager().getPlugin("neutron-n3fs").isPresent()) {
            proxy.getEventManager().register(this, new NeutronN3FSHook());
        }

        // register chat listener
        proxy.getEventManager().register(this, new GChatListener(this));

        // register command
        proxy.getCommandManager().register("gchat", new GChatCommand(this), "globalchat");

        // init api singleton
        GChat.setApi(this);
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        // null the api singleton
        GChat.setApi(null);
    }

    @Override
    public boolean registerPlaceholder(Placeholder placeholder) {
        return placeholders.add(placeholder);
    }

    @Override
    public boolean unregisterPlaceholder(Placeholder placeholder) {
        return placeholders.remove(placeholder);
    }

    @Override
    public ImmutableSet<Placeholder> getPlaceholders() {
        return ImmutableSet.copyOf(placeholders);
    }

    @Override
    public List<ChatFormat> getFormats() {
        return config.getFormats();
    }

    @Override
    public String replacePlaceholders(Player player, String text) {
        if (text == null || text.isEmpty() || placeholders.isEmpty()) {
            return text;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            String definition = matcher.group(1);
            String replacement = null;

            for (Placeholder placeholder : placeholders) {
                replacement = placeholder.getReplacement(player, definition);
                if (replacement != null) {
                    break;
                }
            }

            if (replacement != null) {
                text = text.replace("{" + definition + "}", replacement);
            }
        }

        return text;
    }

    @Override
    public Optional<ChatFormat> getFormat(Player player) {
        return config.getFormats().stream()
                .filter(f -> f.canUse(player))
                .findFirst();
    }

    @Subscribe
    public boolean onReload(ProxyReloadEvent event) {
        return reloadConfig();
    }

    public boolean reloadConfig() {
        try {
            config = loadConfig();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private GChatConfig loadConfig() throws Exception {
        TypeSerializerCollection serializerCollection = TypeSerializerCollection.create();
        ConfigurateComponentSerializer.configurate().addSerializersTo(serializerCollection);

        ConfigurationOptions options = ConfigurationOptions.defaults()
                .withSerializers(serializerCollection);

        ConfigurationNode configNode = YAMLConfigurationLoader.builder()
                .setDefaultOptions(options)
                .setFile(getBundledFile("config.yml"))
                .build()
                .load();
        return new GChatConfig(configNode);
    }

    private File getBundledFile(String name) {
        File file = new File(dataDirectory.toFile(), name);

        if (!file.exists()) {
            dataDirectory.toFile().mkdir();
            try (InputStream in = GChatPlugin.class.getResourceAsStream("/" + name)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    ProxyServer getProxy() {
        return proxy;
    }

    PluginDescription getDescription() {
        return proxy.getPluginManager().getPlugin("gchat-velocity").map(PluginContainer::getDescription).orElse(null);
    }

    public Logger getLogger() {
        return this.logger;
    }

    public GChatConfig getConfig() {
        return this.config;
    }
}
