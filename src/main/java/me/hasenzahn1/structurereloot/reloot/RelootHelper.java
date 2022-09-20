package me.hasenzahn1.structurereloot.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
import me.hasenzahn1.structurereloot.listeners.EntityListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.Lootable;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class RelootHelper {

    public static void relootOneBlock(LootBlockValue value){
        Location loc = value.getLoc();
        loc.getBlock().setType(Material.AIR); //Reset block
        loc.getBlock().setType(value.getBlockMaterial()); //Set Block
        // Bukkit.broadcastMessage(value + "");
        if(loc.getBlock().getBlockData() instanceof Directional){
            //Bukkit.broadcastMessage("Directional");
            Directional data = ((Directional) loc.getBlock().getBlockData());
            data.setFacing(value.getFacing()); //Set Facing Direction
            loc.getBlock().setBlockData(data);
        }
        if(loc.getBlock().getState() instanceof Chest){
            //Bukkit.broadcastMessage("Lootable");
            Chest ltbstate = ((Chest) loc.getBlock().getState());
            ltbstate.setLootTable(value.getLootTable()); //Set LootTable
            ltbstate.update();
        }
        if(loc.getBlock().getState() instanceof Dispenser){
            //Bukkit.broadcastMessage("Lootable");
            Dispenser ltbstate = ((Dispenser) loc.getBlock().getState());
            ltbstate.setLootTable(value.getLootTable()); //Set LootTable
            ltbstate.update();
        }
    }

    public static void relootMultipleBlocks(List<LootBlockValue> values){
        StructureReloot.getInstance().getBlockChangeTask().changeBlocks(values);
    }

    public static void relootOneEntity(LootEntityValue value){
        Location loc = value.getLocation();
        Entity remove = Bukkit.getEntity(value.getUuid()); //Get Old Entity
        if(remove != null){
            remove.teleport(remove.getLocation().subtract(0, 500, 0)); //Remove old Entity if exists
        }
        loc.add(0.5, 0.5, 0.5);
        Entity spawned = loc.getWorld().spawnEntity(loc, value.getEntity()); //Spawn Entity
        if(value.getLootTable() == null && spawned instanceof ItemFrame){
            ((ItemFrame) spawned).setItem(new ItemStack(Material.ELYTRA)); //If itemframe set item
        } else if (spawned instanceof Lootable){
            ((Lootable) spawned).setLootTable(value.getLootTable()); //If StorageMinecart set LootTable
        }
        spawned.getPersistentDataContainer().set(EntityListener.markEntityKey, PersistentDataType.BYTE, (byte) 1); //Mark entity
    }

    public static void relootMultipleEntities(List<LootEntityValue> values){
        StructureReloot.getInstance().getEntityChangeTask().changeEntities(values);
    }

}
