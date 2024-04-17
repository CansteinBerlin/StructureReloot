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

    /**
     * Gets the database for a world. If no database exists a new one with the world's name is created
     *
     * @param world The world of the database
     * @return The database that is created/fetched
     */
    public WorldDatabase getDatabase(World world) {
        if (!databases.containsKey(world)) createDatabase(world);
        return databases.get(world);
    }

    /**
     * Function to create a new Database
     *
     * @param world The world to create the database for
     */
    private void createDatabase(World world) {
        StructureReloot.getInstance().getRelootActivityLogger().logNewWorld(world);
        WorldDatabase database = new WorldDatabase(databasePath, world);
        database.init();
        databases.put(world, database);
    }
}
