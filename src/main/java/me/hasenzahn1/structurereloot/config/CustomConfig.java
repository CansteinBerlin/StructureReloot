package me.hasenzahn1.structurereloot.config;

import com.google.common.base.Charsets;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Getter
public class CustomConfig {

    private File configFile;
    private FileConfiguration config;
    private final JavaPlugin plugin;
    protected boolean isNew;
    private final String name;

    public CustomConfig(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        isNew = false;
        createCustomConfig(plugin, name);
    }

    /**
     * Creates a custom config from a file
     *
     * @param plugin
     * @param name
     */
    private void createCustomConfig(JavaPlugin plugin, String name) {
        configFile = new File(plugin.getDataFolder(), name);
        if (!configFile.exists()) {
            isNew = true;
            configFile.getParentFile().mkdirs();
            if (plugin.getResource(name) != null) plugin.saveResource(name, false);
            else {
                try {
                    configFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Deletes a config file completely
     */
    public void delete() {
        String name = configFile.getName();
        try {
            boolean val = configFile.delete();
            if (!val) {
                plugin.getLogger().severe("Could not delete config file: " + name);
            }
        } catch (SecurityException e) {
            plugin.getLogger().severe("Could not delete config file: " + name);
        }
    }

    /**
     * Reloads the config
     */
    public void reloadConfig() {
        if (!configFile.exists()) {
            createCustomConfig(plugin, configFile.getName());
            return;
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        final InputStream defConfigStream = plugin.getResource(configFile.getName());
        if (defConfigStream == null) {
            return;
        }

        config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }

    /**
     * Saves the config
     */
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
