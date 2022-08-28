package me.hasenzahn1.structurereloot.database;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.tables.blocks.BlockTable;
import me.hasenzahn1.structurereloot.databasesystem.Database;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;

public class BlocksDatabase extends Database {


    public BlocksDatabase(String databasePath, World world) {
        super(StructureReloot.getInstance(), databasePath + "/" + world.getName());

        addTable(new BlockTable(this, world));
    }

    public void addBlock(LootBlockValue value){
        getTable(BlockTable.class).addBlock(value);
    }

    public LootBlockValue getBlock(Location loc){
        return getTable(BlockTable.class).getBlock(loc);
    }

    public ArrayList<LootBlockValue> getAllBlocks(){
        return getTable(BlockTable.class).getAllBlocks();
    }
}
