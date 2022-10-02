package me.hasenzahn1.structurereloot.database;

import me.hasenzahn1.structurereloot.StructureReloot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.loot.LootTable;

import java.util.logging.Level;

public class LootValue {

    protected Location loc;
    protected LootTable lootTable;

    protected LootValue(Location loc, LootTable lootTable){
        this.loc = loc;
        this.lootTable = lootTable;
    }

    protected LootValue(World world, String loc, NamespacedKey lootTable){
        this.loc = getLocFromString(world, loc);
        this.lootTable = lootTable != null ? Bukkit.getLootTable(lootTable) : null;
    }

    public static Location getLocFromString(World world, String loc) {
        String[] strings = loc.split(",");
        try {
            return new Location(world, Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
        }catch (IndexOutOfBoundsException|NumberFormatException e){
            StructureReloot.LOGGER.log(Level.WARNING, "Malformed location string in database: " + loc);
        }
        return null;
    }

    public Location getLocation(){
        return loc;
    }

    public LootTable getLootTable(){
        return lootTable;
    }

    public String getStringLootTable(){
        return lootTable != null ? lootTable.toString() : "";
    }

    public String getLocationString(){
        return locationToLocationString(loc);
    }

    public static String locationToLocationString(Location loc){
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

}
