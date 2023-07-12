package me.hasenzahn1.structurereloot.database;

import me.hasenzahn1.structurereloot.listeners.EntityListener;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

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
        loc.add(0.5, 0.5, 0.5);

        Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5);
        for(Entity e : entities){
            e.teleport(e.getLocation().add(0, -500, 0));
        }

        //Check for surrounding blocks, if not present place supporting block below
        if(!checkSurroundingBlock(loc, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN) && entity == EntityType.ITEM_FRAME){
            loc.clone().subtract(0, 1, 0).getBlock().setType(Material.PURPUR_BLOCK);
        }

        //Check if the block the item frame is placed does not destroy the frame
        if(!loc.getBlock().getType().isAir()){
            loc.getBlock().setType(Material.AIR);
        }

        Entity spawned = loc.getWorld().spawnEntity(loc, entity); //Spawn Entity
        if (lootTable == null && spawned instanceof ItemFrame) {
            ((ItemFrame) spawned).setItem(new ItemStack(Material.ELYTRA)); //If itemframe set item
            spawned.getPersistentDataContainer().set(EntityListener.markEntityKey, PersistentDataType.BYTE, (byte) 1); //Mark entity

        } else if (spawned instanceof Lootable) {
            ((Lootable) spawned).setLootTable(lootTable); //If StorageMinecart set LootTable
        }
    }

    public boolean checkSurroundingBlock(Location location, BlockFace... faces){
        for(BlockFace face : faces){
            if(location.clone().add(face.getDirection()).getBlock().getType().isSolid()){
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
