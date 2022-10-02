package me.hasenzahn1.structurereloot.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
import me.hasenzahn1.structurereloot.database.WorldDatabase;
import me.hasenzahn1.structurereloot.listeners.EntityListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.Lootable;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RelootHelper {

    public static void relootOneBlock(LootBlockValue value){
        Location loc = value.getLocation();
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

    public static void regenNEntities(World world, int amount, Runnable runnable){
        List<LootEntityValue> levs = StructureReloot.getInstance().getDatabase(world).getAllEntities();
        Collections.shuffle(levs);
        List<LootEntityValue> values = levs.stream().limit(Math.min(levs.size(), amount)).collect(Collectors.toList());

        WorldDatabase database = StructureReloot.getInstance().getDatabase(world);
        StructureReloot.getInstance().getEntityChangeTask().addCallback(runnable);
        database.setCacheRemove(true);
        RelootHelper.relootMultipleEntities(values);
        database.removeMultipleEntities(values);
        database.setCacheRemove(false);
        database.close();
    }

    public static void regenNBlocks(World world, int amount, Runnable runnable){
        List<LootBlockValue> lbvs = StructureReloot.getInstance().getDatabase(world).getAllBlocks();
        Collections.shuffle(lbvs);
        List<LootBlockValue> values = lbvs.stream().limit(Math.min(lbvs.size(), amount)).collect(Collectors.toList());

        WorldDatabase database = StructureReloot.getInstance().getDatabase(world);
        StructureReloot.getInstance().getBlockChangeTask().addCallback(runnable);
        database.setCacheRemove(true);
        RelootHelper.relootMultipleBlocks(values);
        database.removeMultipleBlocks(values);
        database.setCacheRemove(false);
        database.close();
    }

}

