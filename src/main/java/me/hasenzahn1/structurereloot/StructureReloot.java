package me.hasenzahn1.structurereloot;

import me.hasenzahn1.structurereloot.commands.RelootCommand;
import me.hasenzahn1.structurereloot.commands.RelootDebugCommand;
import me.hasenzahn1.structurereloot.commandsystem.CommandManager;
import me.hasenzahn1.structurereloot.config.CustomConfig;
import me.hasenzahn1.structurereloot.config.DefaultConfig;
import org.bukkit.ChatColor;
import org.bukkit.loot.LootTables;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class StructureReloot extends JavaPlugin {

    public static String PREFIX = "§b[§6StructureReloot§b] §r";
    private static StructureReloot instance;
    public static Logger LOGGER;

    private boolean debugMode;
    private String databasePath;


    private CommandManager commandManager;
    private CustomConfig defaultConfig;

    @Override
    public void onEnable() {
        instance = this;
        LOGGER = getLogger();

        initConfigs();
        initDatabase();

        commandManager = new CommandManager(this);
        commandManager.addCommand(new RelootCommand());
        if(debugMode) commandManager.addCommand(new RelootDebugCommand());
    }

    private void initDatabase() {
    }

    private void initConfigs() {
        initDefaultConfig();
    }

    private void initDefaultConfig() {
        defaultConfig = new DefaultConfig();
        PREFIX = ChatColor.translateAlternateColorCodes('&', defaultConfig.getConfig().getString("prefix", PREFIX));
        debugMode = defaultConfig.getConfig().getBoolean("debugMode", false);
        databasePath = defaultConfig.getConfig().getString("databaseFolder", "data");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static StructureReloot getInstance() {
        return instance;
    }

}
