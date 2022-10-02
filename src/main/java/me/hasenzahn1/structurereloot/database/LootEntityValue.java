package me.hasenzahn1.structurereloot.database;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.loot.LootTable;

import java.util.UUID;

public class LootEntityValue extends LootValue{

    private final EntityType entity;
    private final UUID uuid;

    public LootEntityValue(EntityType entity, Location location, LootTable lootTable, UUID uuid) {
        super(location, lootTable);

        this.entity = entity;
        this.uuid = uuid;
    }

    public LootEntityValue(World world, String entityType, String location, NamespacedKey lootTable, String uuid){
        super(world, location, lootTable);

        this.entity = EntityType.valueOf(entityType);
        this.uuid = UUID.fromString(uuid);
    }

    public String getEntityString(){
        return entity.name();
    }

    public String getUUIDString(){
        return uuid.toString();
    }

    public EntityType getEntity() {
        return entity;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "LootEntityValue{" +
                "entity=" + entity +
                ", location=" + loc +
                ", lootTable=" + lootTable +
                ", uuid=" + uuid +
                '}';
    }
}
