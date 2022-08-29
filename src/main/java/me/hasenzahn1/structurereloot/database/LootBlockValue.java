package me.hasenzahn1.structurereloot.database;


import me.hasenzahn1.structurereloot.StructureReloot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.loot.LootTable;

import java.util.logging.Level;

public class LootBlockValue {

    private final Location loc;
    private final LootTable lootTable;

    public LootBlockValue(Location loc, LootTable
            lootTable) {
        this.loc = loc;
        this.lootTable = lootTable;
    }

    public LootBlockValue(World world, String loc, NamespacedKey lootTable){
        this.loc = getLocFromString(world, loc);
        this.lootTable = Bukkit.getLootTable(lootTable);
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
        return lootTable.toString();
    }

    @Override
    public String toString() {
        return "LootBlockValue{" +
                "loc=" + loc +
                ", lootTable=" + lootTable.getKey() +
                '}';
    }
}
