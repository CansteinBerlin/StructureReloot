package me.hasenzahn1.structurereloot.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.loot.LootTable;

import java.util.UUID;

public class LootEntityValue {

    private final EntityType entity;
    private final Location location;
    private final LootTable lootTable;
    private final UUID uuid;

    public LootEntityValue(EntityType entity, Location location, LootTable lootTable, UUID uuid) {
        this.entity = entity;
        this.location = location;
        this.lootTable = lootTable;
        this.uuid = uuid;
    }

    public LootEntityValue(World world, String entityType, String location, NamespacedKey lootTable, String uuid){
        this.entity = EntityType.valueOf(entityType);
        this.location = LootBlockValue.getLocFromString(world, location);
        this.lootTable = lootTable == null ? null : Bukkit.getLootTable(lootTable);
        this.uuid = UUID.fromString(uuid);
    }

    public String getEntityString(){
        return entity.name();
    }

    public String getLocationString(){
        return LootBlockValue.locationToLocationString(location);
    }

    public String getLootTableString(){
        return lootTable != null ? lootTable.toString() : "";
    }

    public String getUUIDString(){
        return uuid.toString();
    }

    public EntityType getEntity() {
        return entity;
    }

    public Location getLocation() {
        return location;
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "LootEntityValue{" +
                "entity=" + entity +
                ", location=" + location +
                ", lootTable=" + lootTable +
                ", uuid=" + uuid +
                '}';
    }
}
