package me.hasenzahn1.structurereloot.config;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class LanguageConfig extends CustomConfig {

    private static LanguageConfig languageConfig = null;

    public LanguageConfig(JavaPlugin plugin) {
        super(plugin, "lang.yml");
        languageConfig = this;
    }

    /**
     * Gets a language String from the config
     *
     * @param key  The language key
     * @param args The args that are replaced
     * @return
     */
    public static String getLang(String key, String... args) {
        if (languageConfig == null) return "LANGUAGE CONFIG NOT LOADED";
        String lang = languageConfig.getConfig().getString(key, "&cUnknown or empty language key please check the config &6" + key);
        for (int i = 0; i + 1 < args.length; i += 2) {
            lang = lang.replace("%" + args[i] + "%", args[i + 1]);
        }

        if (!languageConfig.getConfig().contains(key)) {
            languageConfig.getConfig().set(key, "&cUnknown or empty language key please check the config &6" + key);
            languageConfig.saveConfig();
        }

        return ChatColor.translateAlternateColorCodes('&', lang).replace("\\n", "\n");
    }

    /**
     * Gets a chat color from the config
     *
     * @param key The lang key from the config
     * @return
     */
    public static net.md_5.bungee.api.ChatColor getChatColor(String key) {
        String color = languageConfig.getConfig().getString(key, "MAGIC");

        if (!languageConfig.getConfig().contains(key)) {
            languageConfig.getConfig().set(key, "&cUnknown or empty language key please check the config &6" + key);
            languageConfig.saveConfig();
        }
        return net.md_5.bungee.api.ChatColor.of(color);
    }
}
