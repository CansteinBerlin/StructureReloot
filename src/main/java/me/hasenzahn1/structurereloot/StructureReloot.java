package me.hasenzahn1.structurereloot;

import lombok.Getter;
import lombok.Setter;
import me.hasenzahn1.structurereloot.commands.RelootCommand;
import me.hasenzahn1.structurereloot.commands.RelootDebugCommand;
import me.hasenzahn1.structurereloot.commandsystem.CommandManager;
import me.hasenzahn1.structurereloot.config.CustomConfig;
import me.hasenzahn1.structurereloot.config.LanguageConfig;
import me.hasenzahn1.structurereloot.config.UpdateConfig;
import me.hasenzahn1.structurereloot.database.DatabaseManager;
import me.hasenzahn1.structurereloot.general.AutoRelootScheduler;
import me.hasenzahn1.structurereloot.general.RelootActivityLogger;
import me.hasenzahn1.structurereloot.general.RelootSettings;
import me.hasenzahn1.structurereloot.listeners.BlockListener;
import me.hasenzahn1.structurereloot.listeners.EntityListener;
import me.hasenzahn1.structurereloot.reloot.LootValueProcessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Setter
public final class StructureReloot extends JavaPlugin {

    public static String PREFIX = "§b[§6StructureReloot§b] §r";
    private static StructureReloot instance;

    //Data Handling
    private boolean debugMode;
    private RelootActivityLogger relootActivityLogger;

    //Configs
    private CustomConfig defaultConfig;
    private LanguageConfig languageConfig;
    private UpdateConfig blockUpdateConfig;
    private UpdateConfig entityUpdateConfig;

    //Commands
    private CommandManager commandManager;

    //Database
    private DatabaseManager databaseManager;

    //Automatic Relooting and processing of requested reloots
    private LootValueProcessor lootValueProcessor;
    private AutoRelootScheduler autoRelootScheduler;


    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(RelootSettings.class);

        //Static References
        instance = this;

        //Logging
        relootActivityLogger = new RelootActivityLogger(getLogger());

        //Config and Database
        initConfigs();
        databaseManager = new DatabaseManager("data");

        //Creation of tickable tasks
        lootValueProcessor = new LootValueProcessor();
        autoRelootScheduler = new AutoRelootScheduler();
        autoRelootScheduler.runTaskTimer(this, 20 * 5, 20 * 60);

        //Commands
        commandManager = new CommandManager(this);
        commandManager.addCommand(new RelootCommand());
        commandManager.addCommand(new RelootDebugCommand());

        //Register Listeners
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);


    }


    private void initConfigs() {
        languageConfig = new LanguageConfig(this);
        initDefaultConfig();

        blockUpdateConfig = new UpdateConfig("blockUpdateSettings.yml");
        entityUpdateConfig = new UpdateConfig("entityUpdateSettings.yml");
    }

    public void initDefaultConfig() {
        defaultConfig = new CustomConfig(this, "config.yml");
        PREFIX = ChatColor.translateAlternateColorCodes('&', defaultConfig.getConfig().getString("prefix", PREFIX));
        debugMode = defaultConfig.getConfig().getBoolean("debugMode", false);
        LootValueProcessor.CHANGE_AMOUNT = defaultConfig.getConfig().getInt("changesPerTick", 20);
    }

    @Override
    public void onDisable() {
        autoRelootScheduler.cancel();
    }

    public static StructureReloot getInstance() {
        return instance;
    }
}
