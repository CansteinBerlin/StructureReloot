package me.hasenzahn1.structurereloot.listeners;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
import me.hasenzahn1.structurereloot.util.ClassDebug;
import me.hasenzahn1.structurereloot.util.DebugMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.logging.Level;

public class EntityListener implements Listener {

    NamespacedKey markEntityKey = new NamespacedKey(StructureReloot.getInstance(), "markedRelootEntity");

    /*
        Should not be receive block Loottables as well as plugins calling.
        LootTable.fillInventory(org.bukkit.inventory.Inventory, java.util.Random, LootContext).
     */
    @EventHandler
    public void onLootGenerate(LootGenerateEvent event){
        if(event.isPlugin()) return;
        if(!(event.getInventoryHolder() instanceof StorageMinecart)) return;
        StorageMinecart minecart = (StorageMinecart) event.getInventoryHolder();
        Entity e = Bukkit.getEntity(minecart.getUniqueId());
        if(e == null) return;
        LootEntityValue lev = new LootEntityValue(e.getType(), e.getLocation(), event.getLootTable(), minecart.getUniqueId());
        StructureReloot.getInstance().getDatabase(event.getWorld()).addEntity(lev).close();

        if(StructureReloot.getInstance().isDebugMode()) StructureReloot.LOGGER.log(Level.INFO, "Added new LootEntity to Database at location " + lev.getLocationString() + " with lootTable: " + event.getLootTable());
    }

    /*
        If itemframe is spawned in any way the entity is "marked"
    */
    @EventHandler
    public void onEntitySummon(EntitySpawnEvent event){
        System.out.println(new ClassDebug(event));
        Bukkit.broadcastMessage("ENTITY SPAWNED!");
        if(!event.getEntity().getType().equals(EntityType.ITEM_FRAME)) return;
        Entity e = event.getEntity();
        e.getPersistentDataContainer().set(markEntityKey, PersistentDataType.BYTE, (byte)1);
        Bukkit.broadcastMessage("LOADED ENTITY!");
    }

}
