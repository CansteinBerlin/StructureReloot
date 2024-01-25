package me.hasenzahn1.structurereloot.database;

import me.hasenzahn1.structurereloot.StructureReloot;
import org.bukkit.World;

import java.util.HashMap;

public class DatabaseManager {

    private final String databasePath;
    private final HashMap<World, WorldDatabase> databases;

    public DatabaseManager(String databasePath) {
        this.databasePath = databasePath;
        this.databases = new HashMap<>();
    }

    public WorldDatabase getDatabase(World world) {
        if (!databases.containsKey(world)) createDatabase(world);
        return databases.get(world);
    }

    public void createDatabase(World world) {
        StructureReloot.getInstance().getRelootActivityLogger().logNewWorld(world);
        WorldDatabase database = new WorldDatabase(databasePath, world);
        database.init();
        databases.put(world, database);
    }
}
