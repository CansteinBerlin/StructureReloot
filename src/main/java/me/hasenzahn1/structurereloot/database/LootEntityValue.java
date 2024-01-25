package me.hasenzahn1.structurereloot.database;

import lombok.Getter;
import me.hasenzahn1.structurereloot.listeners.EntityListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.UUID;

@Getter
public class LootEntityValue extends LootValue {

    private final EntityType entity;
    private final UUID uuid;

    public LootEntityValue(EntityType entity, Location location, LootTable lootTable, UUID uuid) {
        super(location, lootTable);

        this.entity = entity;
        this.uuid = uuid;
    }

    public LootEntityValue(World world, String entityType, String location, NamespacedKey lootTable, String uuid) {
        super(world, location, lootTable);

        this.entity = EntityType.valueOf(entityType);
        this.uuid = UUID.fromString(uuid);
    }

    @Override
    public void reloot() {
        location.add(0.5, 0.5, 0.5);

        Collection<Entity> entities = location.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5);
        for (Entity e : entities) {
            e.teleport(e.getLocation().add(0, -500, 0));
        }

        //Check for surrounding blocks, if not present place supporting block below
        if (!checkSurroundingBlock(location, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN) && entity == EntityType.ITEM_FRAME) {
            location.clone().subtract(0, 1, 0).getBlock().setType(Material.PURPUR_BLOCK);
        }

        //Check if the block the item frame is placed does not destroy the frame
        if (!location.getBlock().getType().isAir()) {
            location.getBlock().setType(Material.AIR);
        }

        Entity spawned = location.getWorld().spawnEntity(location, entity); //Spawn Entity
        if (lootTable == null && spawned instanceof ItemFrame) {
            ((ItemFrame) spawned).setItem(new ItemStack(Material.ELYTRA)); //If itemframe set item
            spawned.getPersistentDataContainer().set(EntityListener.markEntityKey, PersistentDataType.BYTE, (byte) 1); //Mark entity

        } else if (spawned instanceof Lootable) {
            ((Lootable) spawned).setLootTable(lootTable); //If StorageMinecart set LootTable
        }
    }

    public boolean checkSurroundingBlock(Location location, BlockFace... faces) {
        for (BlockFace face : faces) {
            if (location.clone().add(face.getDirection()).getBlock().getType().isSolid()) {
                return true;
            }
        }
        return false;
    }

    //Getter and Setter
    public String getEntityString() {
        return entity.name();
    }

    public String getUUIDString() {
        return uuid.toString();
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
