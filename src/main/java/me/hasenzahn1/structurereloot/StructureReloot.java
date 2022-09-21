package me.hasenzahn1.structurereloot;

import me.hasenzahn1.structurereloot.commands.RelootCommand;
import me.hasenzahn1.structurereloot.commands.RelootDebugCommand;
import me.hasenzahn1.structurereloot.commandsystem.CommandManager;
import me.hasenzahn1.structurereloot.config.CustomConfig;
import me.hasenzahn1.structurereloot.config.DefaultConfig;
import me.hasenzahn1.structurereloot.config.LanguageConfig;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
import me.hasenzahn1.structurereloot.database.WorldDatabase;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.listeners.BlockListener;
import me.hasenzahn1.structurereloot.listeners.EntityListener;
import me.hasenzahn1.structurereloot.reloot.BlockChangeTask;
import me.hasenzahn1.structurereloot.reloot.EntityChangeTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.loot.LootTables;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
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

    private BlockChangeTask blockChangeTask;
    private EntityChangeTask entityChangeTask;

    @Override
    public void onEnable() {
        instance = this;
        LOGGER = getLogger();

        initConfigs();
        initDatabase();

        blockChangeTask = new BlockChangeTask();
        entityChangeTask = new EntityChangeTask();

        commandManager = new CommandManager(this);
        commandManager.addCommand(new RelootCommand());
        if(debugMode) commandManager.addCommand(new RelootDebugCommand());

        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
    }

    private void initDatabase() {
        databases = new HashMap<>();
        for(World world : Bukkit.getWorlds()){
            System.out.println(world);
            WorldDatabase database = new WorldDatabase(databasePath, world);
            database.init();
            databases.put(world, database);
        }

        /*
        World world = Bukkit.getWorld("world");
        WorldDatabase database = getDatabase(world);
        database.addBlock(new LootBlockValue(new Location(world, 0, 0, 0), LootTables.BASTION_HOGLIN_STABLE.getLootTable()));
        database.addBlock(new LootBlockValue(new Location(world, 10, 0, 0), LootTables.BASTION_BRIDGE.getLootTable()));
        database.addBlock(new LootBlockValue(new Location(world, 100, 0, 0), LootTables.ANCIENT_CITY.getLootTable()));
        System.out.println(database.getAllBlocks());
        System.out.println("###################");
        System.out.println(database.getBlock(new Location(world, 0, 0, 0)));

        System.out.println("#############################################");
        database.addEntity(new LootEntityValue(EntityType.CHEST_BOAT, new Location(world, 0, 0, 0), LootTables.BASTION_OTHER.getLootTable(), UUID.randomUUID()));
        database.addEntity(new LootEntityValue(EntityType.CHEST_BOAT, new Location(world, 10, 0, 0), LootTables.ABANDONED_MINESHAFT.getLootTable(), UUID.randomUUID()));
        database.addEntity(new LootEntityValue(EntityType.CHEST_BOAT, new Location(world, 100, 0, 0), LootTables.CAVE_SPIDER.getLootTable(), UUID.randomUUID()));
        System.out.println(database.getAllEntities());
        System.out.println("##########");
        System.out.println(database.getEntity(new Location(world, 0, 0, 0)));
        database.close();
        */
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

    private void initConfigs() {
        languageConfig = new LanguageConfig(this);
        initDefaultConfig();
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

    public BlockChangeTask getBlockChangeTask() {
        return blockChangeTask;
    }

    public EntityChangeTask getEntityChangeTask() {
        return entityChangeTask;
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
}
