package me.hasenzahn1.structurereloot.database;


import me.hasenzahn1.structurereloot.StructureReloot;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.loot.LootTable;

import java.util.logging.Level;

public class LootBlockValue {

    private final Location loc;
    private final LootTable lootTable;
    private final Material blockMaterial;
    private final BlockFace facing;

    public LootBlockValue(Location loc, LootTable lootTable) {
        this.loc = loc;
        this.lootTable = lootTable;
        blockMaterial = loc.getBlock().getType();
        BlockData data = loc.getBlock().getBlockData();
        if(data instanceof Directional){
            facing = ((Directional) data).getFacing();
        }else{
            facing = BlockFace.NORTH;
        }
    }

    public LootBlockValue(Location loc, LootTable lootTable, Material blockMaterial, BlockFace facing) {
        this.loc = loc;
        this.lootTable = lootTable;
        this.blockMaterial = blockMaterial;
        this.facing = facing;
    }

    public LootBlockValue(World world, String loc, NamespacedKey lootTable, String blockMaterial, String facing) {
        this.loc = getLocFromString(world, loc);
        this.lootTable = Bukkit.getLootTable(lootTable);
        this.blockMaterial = Material.valueOf(blockMaterial);
        this.facing = BlockFace.valueOf(facing);
    }

    private Location getLocFromString(World world, String loc) {
        String[] strings = loc.split(",");
        try {
            return new Location(world, Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
        }catch (IndexOutOfBoundsException|NumberFormatException e){
            StructureReloot.LOGGER.log(Level.WARNING, "Malformed location string in database: " + loc);
        }
        return null;
    }

    public String getLocationString(){
        return locationToLocationString(loc);
    }

    public static String locationToLocationString(Location loc){
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    public String getBlockMaterialString(){
        return blockMaterial.name();
    }

    public String getFacingString(){
        return facing.name();
    }

    public String getStringLootTable(){
        return lootTable.toString();
    }

    @Override
    public String toString() {
        return "LootBlockValue{" +
                "loc=" + loc +
                ", lootTable=" + lootTable.toString() +
                ", blockMaterial=" + blockMaterial +
                ", facing=" + facing +
                '}';
    }
}
