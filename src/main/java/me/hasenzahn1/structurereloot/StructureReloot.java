package me.hasenzahn1.structurereloot;

import me.hasenzahn1.structurereloot.commands.RelootCommand;
import me.hasenzahn1.structurereloot.commands.RelootDebugCommand;
import me.hasenzahn1.structurereloot.commandsystem.CommandManager;
import me.hasenzahn1.structurereloot.config.CustomConfig;
import me.hasenzahn1.structurereloot.config.LanguageConfig;
import me.hasenzahn1.structurereloot.config.UpdateConfig;
import me.hasenzahn1.structurereloot.database.WorldDatabase;
import me.hasenzahn1.structurereloot.general.AutoRelootScheduler;
import me.hasenzahn1.structurereloot.general.ChangesPerDay;
import me.hasenzahn1.structurereloot.general.RelootSettings;
import me.hasenzahn1.structurereloot.listeners.BlockListener;
import me.hasenzahn1.structurereloot.listeners.EntityListener;
import me.hasenzahn1.structurereloot.reloot.LootValueChangeTask;
import me.hasenzahn1.structurereloot.reloot.RelootHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public final class StructureReloot extends JavaPlugin {

    public static String PREFIX = "§b[§6StructureReloot§b] §r";
    private static StructureReloot instance;
    public static Logger LOGGER;

    //Data Handling
    private boolean debugMode;
    private String databasePath;

    //Configs
    private CustomConfig defaultConfig;
    private LanguageConfig languageConfig;
    private UpdateConfig blockUpdateConfig;
    private UpdateConfig entityUpdateConfig;

    private CommandManager commandManager;
    private HashMap<World, WorldDatabase> databases;

    private LootValueChangeTask lootValueChangeTask;


    private AutoRelootScheduler autoRelootScheduler;

    private ChangesPerDay changes;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(RelootSettings.class);

        instance = this;
        LOGGER = getLogger();

        initConfigs();
        databases = new HashMap<>();

        changes = new ChangesPerDay();

        lootValueChangeTask = new LootValueChangeTask();

        commandManager = new CommandManager(this);
        commandManager.addCommand(new RelootCommand());
        commandManager.addCommand(new RelootDebugCommand());

        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);

        relootElementsInWorld(true);

        autoRelootScheduler = new AutoRelootScheduler();
        autoRelootScheduler.runTaskTimer(this, 20 * 5, 20 * 5);

    }

    public void relootElementsInWorld(boolean isStartup) {
        List<World> neededBlockUpdateSettings = blockUpdateConfig.getNeededUpdates();
        List<World> neededEntityUpdateSettings = entityUpdateConfig.getNeededUpdates();
        boolean updated = false;

        //Blocks
        for (World world : neededBlockUpdateSettings) {
            RelootSettings settings = blockUpdateConfig.getSettingsForWorld(world);
            if (isStartup != settings.isRelootOnStartup()) {
                continue;
            }

            //Bukkit.broadcastMessage("Update blocks in world: " + world.getName());
            RelootHelper.regenNBlocks(world, settings.getMaxRelootAmount(), null);
            updated = true;
            settings.nextDate();
        }

        if (updated) blockUpdateConfig.update();

        //Entities
        updated = false;
        for (World world : neededEntityUpdateSettings) {
            RelootSettings settings = entityUpdateConfig.getSettingsForWorld(world);

            if (isStartup != settings.isRelootOnStartup()) {
                continue;
            }

            //Bukkit.broadcastMessage("Update entity in world: " + world.getName());
            RelootHelper.regenNEntities(world, settings.getMaxRelootAmount(), null);
            updated = true;
            settings.nextDate();
        }

        if (updated) entityUpdateConfig.update();
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
        databasePath = "data";
        LootValueChangeTask.CHANGE_AMOUNT = defaultConfig.getConfig().getInt("changesPerTick", 20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static StructureReloot getInstance() {
        return instance;
    }

    public WorldDatabase getDatabase(World world) {
        if (!databases.containsKey(world)) createDatabase(world);
        return databases.get(world);
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDefaultConfig(CustomConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public CustomConfig getDefaultConfig() {
        return defaultConfig;
    }

    public LanguageConfig getLanguageConfig() {
        return languageConfig;
    }

    public UpdateConfig getBlockUpdateConfig() {
        return blockUpdateConfig;
    }

    public UpdateConfig getEntityUpdateConfig() {
        return entityUpdateConfig;
    }

    public void setBlockUpdateConfig(UpdateConfig blockUpdateConfig) {
        this.blockUpdateConfig = blockUpdateConfig;
    }

    public void setEntityUpdateConfig(UpdateConfig entityUpdateConfig) {
        this.entityUpdateConfig = entityUpdateConfig;
    }

    public ChangesPerDay getChangesPerDay() {
        return changes;
    }

    public LootValueChangeTask getLootValueChangeTask() {
        return lootValueChangeTask;
    }

    public void createDatabase(World world) {
        LOGGER.info("Found new world with name: " + world.getName());
        WorldDatabase database = new WorldDatabase(databasePath, world);
        database.init();
        databases.put(world, database);
    }
}
