package net.serble.serblebungeeplugin;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class ConfigUtil {
    public static Configuration configuration;
    private static final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);

    synchronized void createConfig() {
        Main.plugin.getLogger().info("Generating default configuration file. Edit it and restart your network.");
        File f = Main.plugin.getDataFolder();
        File conf = new File(f, "config.yml");
        try {
            boolean success = Main.plugin.getDataFolder().mkdir();
            if (!success) {
                Main.plugin.getLogger().info("Failed to create data folder.");
            }
            success = conf.createNewFile();
            if (!success) {
                Main.plugin.getLogger().info("Failed to create config file.");
            }
            Configuration config = provider.load(conf);
            configuration = config;

            if (config.getString("fallback-server").isEmpty()) {
                config.set("fallback-server", "main");
            }
            if (config.getString("limbo-server").isEmpty()) {
                config.set("limbo-server", "limbo");
            }
            if (config.getString("hub-gamemode").isEmpty()) {
                config.set("hub-gamemode", "main");
            }
            if (config.getStringList("ignore-fallback-keywords").isEmpty()) {
                config.set("ignore-fallback-keywords", Collections.singletonList("banned"));
            }
            if (!config.contains("kick-listener")) {
                config.set("kick-listener", true);
            }
            provider.save(config, conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
