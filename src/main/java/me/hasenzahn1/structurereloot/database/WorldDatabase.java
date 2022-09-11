package me.hasenzahn1.structurereloot.database;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.tables.BlockTable;
import me.hasenzahn1.structurereloot.database.tables.EntityTable;
import me.hasenzahn1.structurereloot.databasesystem.Database;
import org.bukkit.Location;
import org.bukkit.World;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;

public class WorldDatabase extends Database {


    public WorldDatabase(String databasePath, World world) {
        super(StructureReloot.getInstance(), databasePath + "/" + world.getName());

        addTable(new BlockTable(this, world));
        addTable(new EntityTable(this, world));
    }

    public WorldDatabase addBlock(LootBlockValue value){
        getTable(BlockTable.class).addBlock(value);
        return this;
    }

    public LootBlockValue getBlock(Location loc){
        return getTable(BlockTable.class).getBlock(loc);
    }

    public ArrayList<LootBlockValue> getAllBlocks(){
        return getTable(BlockTable.class).getAllBlocks();
    }

    public void removeLootBlockValue(LootBlockValue value){
        getTable(BlockTable.class).removeLootBlockValue(value);
    }

    public WorldDatabase addEntity(LootEntityValue value){
        getTable(EntityTable.class).addEntity(value);
        return this;
    }

    public LootEntityValue getEntity(Location location){
        return getTable(EntityTable.class).getEntity(location);
    }

    public ArrayList<LootEntityValue> getAllEntities(){
        return getTable(EntityTable.class).getAllEntities();
    }

    public void removeLootEntityValue(LootEntityValue value){
        getTable(EntityTable.class).removeLootEntityValue(value);
    }

    public void removeAllEntitys(){
        ArrayList<LootEntityValue> values = getAllEntities();
        for(LootEntityValue e : values){
            removeLootEntityValue(e);
        }
    }

    public void removeAllBlocks(){
        ArrayList<LootBlockValue> values = getAllBlocks();
        for(LootBlockValue e : values){
            removeLootBlockValue(e);
        }
    }
}
