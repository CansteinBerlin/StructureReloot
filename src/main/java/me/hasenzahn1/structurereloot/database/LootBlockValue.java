package me.hasenzahn1.structurereloot.database;


import me.hasenzahn1.structurereloot.StructureReloot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.loot.LootTables;

import java.util.logging.Level;

public class LootBlockValue {

    private final Location loc;
    private final LootTables lootTable;

    public LootBlockValue(Location loc, LootTables lootTable) {
        this.loc = loc;
        this.lootTable = lootTable;
    }

    public LootBlockValue(World world, String loc, String lootTable){
        this.loc = getLocFromString(world, loc);
        this.lootTable = LootTables.valueOf(lootTable);
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

    public String getStringLootTable(){
        return lootTable.name();
    }

}
