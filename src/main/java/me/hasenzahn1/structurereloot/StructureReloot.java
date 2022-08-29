package me.hasenzahn1.structurereloot;

import me.hasenzahn1.structurereloot.commands.RelootCommand;
import me.hasenzahn1.structurereloot.commands.RelootDebugCommand;
import me.hasenzahn1.structurereloot.commandsystem.CommandManager;
import me.hasenzahn1.structurereloot.config.CustomConfig;
import me.hasenzahn1.structurereloot.config.DefaultConfig;
import me.hasenzahn1.structurereloot.database.BlocksDatabase;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.databasesystem.Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Logger;

public final class StructureReloot extends JavaPlugin {

    public static String PREFIX = "§b[§6StructureReloot§b] §r";
    private static StructureReloot instance;
    public static Logger LOGGER;

    private boolean debugMode;
    private String databasePath;


    private CommandManager commandManager;
    private CustomConfig defaultConfig;
    private HashMap<World, BlocksDatabase>  databases;

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
        databases = new HashMap<>();
        for(World world : Bukkit.getWorlds()){
            System.out.println(world);
            BlocksDatabase database = new BlocksDatabase(databasePath, world);
            database.init();
            databases.put(world, database);
        }

        /*
        World world = Bukkit.getWorld("world");
        BlocksDatabase database = getDatabase(world);
        database.addBlock(new LootBlockValue(new Location(world, 0, 0, 0), LootTables.BASTION_HOGLIN_STABLE.getLootTable()));
        database.addBlock(new LootBlockValue(new Location(world, 10, 0, 0), LootTables.BASTION_BRIDGE.getLootTable()));
        database.addBlock(new LootBlockValue(new Location(world, 100, 0, 0), LootTables.ANCIENT_CITY.getLootTable()));
        System.out.println(database.getAllBlocks());
        System.out.println("###################");
        System.out.println(database.getBlock(new Location(world, 0, 0, 0)));
        database.close();

         */

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

    public BlocksDatabase getDatabase(World world){
        return databases.get(world);
    }

}
