package me.hasenzahn1.structurereloot.database;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.config.LanguageConfig;
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
    //get Value
    public LootBlockValue getBlock(Location loc) {
        if (loc == null) return null;
        return getTable(BlockTable.class).getBlock(loc);
    }

    public ArrayList<LootBlockValue> getAllBlocks() {
        return getTable(BlockTable.class).getAllBlocks();
    }


    //add Value
    public WorldDatabase addBlock(LootBlockValue value) {
        getTable(BlockTable.class).addBlock(value);
        return this;
    }

    public void addMultipleBlocks(List<LootBlockValue> values) {
        getTable(BlockTable.class).addMultipleBlocks(values);
    }


    //remove Value
    public void removeBlock(LootBlockValue value) {
        getTable(BlockTable.class).removeBlock(value);
    }

    public void removeMultipleBlocks(List<LootBlockValue> values) {
        getTable(BlockTable.class).removeMultipleBlocks(values);
    }

    public void removeAllBlocks() {
        removeMultipleBlocks(getAllBlocks());
    }


    //############# Entities #############//

    //get Value
    public LootEntityValue getEntity(Location location) {
        if (location == null) return null;
        return getTable(EntityTable.class).getEntity(location);
    }

    public ArrayList<LootEntityValue> getAllEntities() {
        return getTable(EntityTable.class).getAllEntities();
    }


    //add Value
    public WorldDatabase addEntity(LootEntityValue value) {
        getTable(EntityTable.class).addEntity(value);
        return this;
    }

    public void addMultipleEntities(List<LootEntityValue> values) {
        getTable(EntityTable.class).addMultipleEntities(values);
    }


    //remove Value
    public void removeEntity(LootEntityValue value) {
        getTable(EntityTable.class).removeEntity(value);
    }

    public void removeMultipleEntities(List<LootEntityValue> values) {
        getTable(EntityTable.class).removeMultipleEntities(values);
    }

    public void removeAllEntitys() {
        removeMultipleEntities(getAllEntities());
    }


    //############# Caching ##############//
    public void setCacheRemove(boolean value) {
        getTable(EntityTable.class).setCacheRemove(value);
        getTable(BlockTable.class).setCacheRemove(value);
    }
}
