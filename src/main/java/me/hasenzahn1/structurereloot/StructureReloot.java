package me.hasenzahn1.structurereloot;

import me.hasenzahn1.structurereloot.commands.RelootCommand;
import me.hasenzahn1.structurereloot.commandsystem.CommandManager;
import me.hasenzahn1.structurereloot.config.CustomConfig;
import me.hasenzahn1.structurereloot.config.DefaultConfig;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class StructureReloot extends JavaPlugin {

    public static String PREFIX = "§b[§6StructureReloot§b] §r";
    private static StructureReloot instance;
    private static Logger logger;


    private CommandManager commandManager;
    private CustomConfig defaultConfig;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        initConfigs();

        commandManager = new CommandManager(this);
        commandManager.addCommand(new RelootCommand());
    }

    private void initConfigs() {
        initDefaultConfig();
    }

    private void initDefaultConfig() {
        defaultConfig = new DefaultConfig();
        PREFIX = ChatColor.translateAlternateColorCodes('&', defaultConfig.getConfig().getString("prefix", PREFIX));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static StructureReloot getInstance() {
        return instance;
    }

    @Override
    public static Logger getLogger() {
        return logger;
    }
}
