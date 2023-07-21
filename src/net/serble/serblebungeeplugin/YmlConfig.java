package net.serble.serblebungeeplugin;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class YmlConfig {

    private Configuration configuration;
    private final File file;

    public YmlConfig(File file) throws IOException {
        this.file = file;
        load(file);
    }

    public YmlConfig(String fileName) throws IOException {
        this(new File(ProxyServer.getInstance().getPluginsFolder(), fileName));
    }

    public void save() throws IOException {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
    }

    public void load(File file) throws IOException {
        if (!file.exists()){
            file.createNewFile();
        }

        this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
    }

    public void load(String fileName) throws IOException {
        load(new File(ProxyServer.getInstance().getPluginsFolder(), fileName));
    }

    public void checkOrSet(AtomicBoolean changed, String key, Object value) {
        if (!configuration.contains(key)) {
            // Conversion of Map value to Configuration Section isn't straightforward in bungee API
            // Assuming for fair conversion, the value is directly set as in other type.
            configuration.set(key, value);
            changed.set(true);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}