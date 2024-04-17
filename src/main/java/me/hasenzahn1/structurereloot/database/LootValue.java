package me.hasenzahn1.structurereloot.database;

import lombok.Getter;
import me.hasenzahn1.structurereloot.StructureReloot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.loot.LootTable;

import java.util.Objects;
import java.util.logging.Level;

@Getter
public abstract class LootValue {

    protected Location location;
    protected LootTable lootTable;

    public abstract void reloot();

    protected LootValue(Location location, LootTable lootTable) {
        this.location = location;
        this.lootTable = lootTable;
    }

    protected LootValue(World world, String location, NamespacedKey lootTable) {
        this.location = getLocFromString(world, location);
        this.lootTable = lootTable != null ? Bukkit.getLootTable(lootTable) : null;
    }

    public String getStringLootTable() {
        return lootTable != null ? lootTable.toString() : "";
    }

    public String getLocationString() {
        return locationToLocationString(location);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LootValue value = (LootValue) o;
        return Objects.equals(location, value.location) && Objects.equals(lootTable, value.lootTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, lootTable);
    }

    public static String locationToLocationString(Location loc) {
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    /**
     * Gets a Location from a world and a string
     *
     * @param world The world the Location is in
     * @param loc   The String representation of the location. Probably from the database
     * @return The location created from the database
     */
    public static Location getLocFromString(World world, String loc) {
        String[] strings = loc.split(",");
        try {
            return new Location(world, Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            StructureReloot.getInstance().getRelootActivityLogger().log(Level.WARNING, "Malformed location string in database: " + loc);
        }
        return null;
    }

}
