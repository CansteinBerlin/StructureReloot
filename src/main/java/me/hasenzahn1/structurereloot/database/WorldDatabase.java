package me.hasenzahn1.structurereloot.database;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.tables.BlockTable;
import me.hasenzahn1.structurereloot.database.tables.EntityTable;
import me.hasenzahn1.structurereloot.databasesystem.Database;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class WorldDatabase extends Database {

    public WorldDatabase(String databasePath, World world) {
        super(StructureReloot.getInstance(), databasePath + "/" + world.getName());

        addTable(new BlockTable(this, world));
        addTable(new EntityTable(this, world));
    }

    //############## Blocks ##############//

    /**
     * Get a block at a specific location from the config
     *
     * @param loc The location the block is at
     * @return a block if it exists in the database, or null if it does not exist
     */
    public LootBlockValue getBlock(Location loc) {
        if (loc == null) return null;
        return getTable(BlockTable.class).getBlock(loc);
    }

    /**
     * Gets all the blocks from the database
     *
     * @return
     */
    public ArrayList<LootBlockValue> getAllBlocks() {
        return getTable(BlockTable.class).getAllBlocks();
    }


    /**
     * Adds a new block to the database
     *
     * @param value
     * @return
     */
    public WorldDatabase addBlock(LootBlockValue value) {
        getTable(BlockTable.class).addBlock(value);
        return this;
    }

    /**
     * Add multiple blocks to the database
     *
     * @param values
     */
    public void addMultipleBlocks(List<LootBlockValue> values) {
        getTable(BlockTable.class).addMultipleBlocks(values);
    }


    /**
     * Remove a block from the database
     *
     * @param value
     */
    public void removeBlock(LootBlockValue value) {
        getTable(BlockTable.class).removeBlock(value);
    }

    /**
     * Remove multiple blocks from the database
     *
     * @param values
     */
    public void removeMultipleBlocks(List<LootBlockValue> values) {
        getTable(BlockTable.class).removeMultipleBlocks(values);
    }

    /**
     * Remove all blocks from the database
     */
    public void removeAllBlocks() {
        removeMultipleBlocks(getAllBlocks());
    }


    //############# Entities #############//

    /**
     * Add an entity to the database
     *
     * @param location
     * @return
     */
    public LootEntityValue getEntity(Location location) {
        if (location == null) return null;
        return getTable(EntityTable.class).getEntity(location);
    }

    /**
     * Get all entities from the config
     *
     * @return
     */
    public ArrayList<LootEntityValue> getAllEntities() {
        return getTable(EntityTable.class).getAllEntities();
    }


    /**
     * Add an entity to the database
     *
     * @param value
     * @return
     */
    public WorldDatabase addEntity(LootEntityValue value) {
        getTable(EntityTable.class).addEntity(value);
        return this;
    }

    /**
     * Add multiple entities to the database
     *
     * @param values
     */
    public void addMultipleEntities(List<LootEntityValue> values) {
        getTable(EntityTable.class).addMultipleEntities(values);
    }


    /**
     * Remove an entity from the database
     *
     * @param value
     */
    public void removeEntity(LootEntityValue value) {
        getTable(EntityTable.class).removeEntity(value);
    }

    /**
     * Remove multiple entities from the database
     *
     * @param values
     */
    public void removeMultipleEntities(List<LootEntityValue> values) {
        getTable(EntityTable.class).removeMultipleEntities(values);
    }

    /**
     * Remove all entities from the database
     */
    public void removeAllEntitys() {
        removeMultipleEntities(getAllEntities());
    }
}
