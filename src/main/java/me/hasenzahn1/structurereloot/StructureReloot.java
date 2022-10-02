package me.hasenzahn1.structurereloot;

import me.hasenzahn1.structurereloot.autoupdate.AutoRelootScheduler;
import me.hasenzahn1.structurereloot.autoupdate.ChangesPerDay;
import me.hasenzahn1.structurereloot.autoupdate.RelootSettings;
import me.hasenzahn1.structurereloot.commands.RelootCommand;
import me.hasenzahn1.structurereloot.commands.RelootDebugCommand;
import me.hasenzahn1.structurereloot.commandsystem.CommandManager;
import me.hasenzahn1.structurereloot.config.CustomConfig;
import me.hasenzahn1.structurereloot.config.DefaultConfig;
import me.hasenzahn1.structurereloot.config.LanguageConfig;
import me.hasenzahn1.structurereloot.config.update.BlockUpdateConfig;
import me.hasenzahn1.structurereloot.config.update.EntityUpdateConfig;
import me.hasenzahn1.structurereloot.database.WorldDatabase;
import me.hasenzahn1.structurereloot.listeners.BlockListener;
import me.hasenzahn1.structurereloot.listeners.EntityListener;
import me.hasenzahn1.structurereloot.reloot.BlockChangeTask;
import me.hasenzahn1.structurereloot.reloot.EntityChangeTask;
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

    private boolean debugMode;
    private String databasePath;


    private CommandManager commandManager;
    private CustomConfig defaultConfig;
    private LanguageConfig languageConfig;
    private HashMap<World, WorldDatabase>  databases;

    private LootValueChangeTask lootValueChangeTask;

    private BlockUpdateConfig blockUpdateConfig;
    private EntityUpdateConfig entityUpdateConfig;
    private AutoRelootScheduler autoRelootScheduler;

    private ChangesPerDay changes;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(RelootSettings.class);

        instance = this;
        LOGGER = getLogger();

        initConfigs();
        initDatabase();

        changes = new ChangesPerDay();

        lootValueChangeTask = new LootValueChangeTask();

        commandManager = new CommandManager(this);
        commandManager.addCommand(new RelootCommand());
        if(debugMode) commandManager.addCommand(new RelootDebugCommand());

        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);

        relootElementsInWorld(true);

        autoRelootScheduler = new AutoRelootScheduler();
        autoRelootScheduler.runTaskTimer(this, 20, 20*5);

    }

    public void relootElementsInWorld(boolean isStartup) {
        List<World> neededBlockUpdateSettings = blockUpdateConfig.getNeededUpdates();
        List<World> neededEntityUpdateSettings = entityUpdateConfig.getNeededUpdates();
        boolean updated = false;

        //Blocks
        for(World world : neededBlockUpdateSettings){
            RelootSettings settings = blockUpdateConfig.getSettingsForWorld(world);
            if(isStartup != settings.isRelootOnStartup()){
                continue;
            }

            //Bukkit.broadcastMessage("Update blocks in world: " + world.getName());
            RelootHelper.regenNBlocks(world, settings.getMaxRelootAmount(), null);
            updated = true;
            settings.nextDate();
        }

        if(updated) blockUpdateConfig.update();

        //Entities
        updated = false;
        for(World world : neededEntityUpdateSettings){
            RelootSettings settings = entityUpdateConfig.getSettingsForWorld(world);

            if(isStartup != settings.isRelootOnStartup()){
                continue;
            }

            //Bukkit.broadcastMessage("Update entity in world: " + world.getName());
            RelootHelper.regenNEntities(world, settings.getMaxRelootAmount(), null);
            updated = true;
            settings.nextDate();
        }

        if(updated) entityUpdateConfig.update();
    }

    private void initDatabase() {
        databases = new HashMap<>();
        for(World world : Bukkit.getWorlds()){
            System.out.println(world);
            WorldDatabase database = new WorldDatabase(databasePath, world);
            database.init();
            databases.put(world, database);
        }

    }

    public static String getLang(String key, String... args) {
        String lang = StructureReloot.getInstance().languageConfig.getConfig().getString(key, "&cUnknown or empty language key please check the config &6" + key);
        for (int i = 0; i + 1 < args.length; i += 2) {
            lang = lang.replace("%" + args[i] + "%", args[i + 1]);
        }

        if(!StructureReloot.getInstance().languageConfig.getConfig().contains(key)){
            StructureReloot.getInstance().languageConfig.getConfig().set(key, "&cUnknown or empty language key please check the config &6" + key);
            StructureReloot.getInstance().languageConfig.saveConfig();
        }

        return ChatColor.translateAlternateColorCodes('&', lang).replace("\\n", "\n");
    }

    public static net.md_5.bungee.api.ChatColor getChatColor(String key){
        String color = StructureReloot.getInstance().languageConfig.getConfig().getString(key, "MAGIC");

        if(!StructureReloot.getInstance().languageConfig.getConfig().contains(key)){
            StructureReloot.getInstance().languageConfig.getConfig().set(key, "&cUnknown or empty language key please check the config &6" + key);
            StructureReloot.getInstance().languageConfig.saveConfig();
        }
        return net.md_5.bungee.api.ChatColor.of(color);
    }

    private void initConfigs() {
        languageConfig = new LanguageConfig(this);
        initDefaultConfig();

        blockUpdateConfig = new BlockUpdateConfig();
        entityUpdateConfig = new EntityUpdateConfig();
    }

    public void initDefaultConfig() {
        defaultConfig = new DefaultConfig();
        PREFIX = ChatColor.translateAlternateColorCodes('&', defaultConfig.getConfig().getString("prefix", PREFIX));
        debugMode = defaultConfig.getConfig().getBoolean("debugMode", false);
        databasePath = defaultConfig.getConfig().getString("databaseFolder", "data");
        BlockChangeTask.BLOCK_CHANGE_AMOUNT = defaultConfig.getConfig().getInt("blockChangesPerTick", 30);
        EntityChangeTask.ENTITY_CHANGE_AMOUNT = defaultConfig.getConfig().getInt("entityChangesPerTick", 30);
    }

    public void reloadLanguageConfig(){
        if(languageConfig != null){
            languageConfig.reloadConfig();
        }
        else{
            languageConfig = new LanguageConfig(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static StructureReloot getInstance() {
        return instance;
    }

    public WorldDatabase getDatabase(World world){
        return databases.get(world);
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDefaultConfig(CustomConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public void setLanguageConfig(LanguageConfig languageConfig) {
        this.languageConfig = languageConfig;
    }

    public CustomConfig getDefaultConfig() {
        return defaultConfig;
    }

    public LanguageConfig getLanguageConfig() {
        return languageConfig;
    }

    public BlockUpdateConfig getBlockUpdateConfig() {
        return blockUpdateConfig;
    }

    public EntityUpdateConfig getEntityUpdateConfig() {
        return entityUpdateConfig;
    }

    public void setBlockUpdateConfig(BlockUpdateConfig blockUpdateConfig) {
        this.blockUpdateConfig = blockUpdateConfig;
    }

    public void setEntityUpdateConfig(EntityUpdateConfig entityUpdateConfig) {
        this.entityUpdateConfig = entityUpdateConfig;
    }

    public ChangesPerDay getChangesPerDay(){
        return changes;
    }

    public LootValueChangeTask getLootValueChangeTask() {
        return lootValueChangeTask;
    }
}
