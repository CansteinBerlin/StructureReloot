package me.hasenzahn1.structurereloot.listeners;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class EntityListener implements Listener {

    public static NamespacedKey markEntityKey = new NamespacedKey(StructureReloot.getInstance(), "markedRelootEntity");

    /*
        Should not receive block Loottables as well as plugins calling.
        LootTable.fillInventory(org.bukkit.inventory.Inventory, java.util.Random, LootContext).
     */
    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        if (event.isPlugin()) return;
        if (!(event.getInventoryHolder() instanceof StorageMinecart)) return;
        StorageMinecart minecart = (StorageMinecart) event.getInventoryHolder();
        Entity e = Bukkit.getEntity(minecart.getUniqueId());
        if (e == null) return;
        LootEntityValue lev = new LootEntityValue(e.getType(), e.getLocation(), event.getLootTable(), minecart.getUniqueId());
        StructureReloot.getInstance().getDatabase(event.getWorld()).addEntity(lev);

        if (StructureReloot.getInstance().isDebugMode())
            StructureReloot.LOGGER.log(Level.INFO, "Added new LootEntity to Database at location " + lev.getLocationString() + " with lootTable: " + event.getLootTable());
    }


    /*
        If an Itemframe is spawned mark it
     */
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        //If a chunk is loaded in world with no database create a database
        if (StructureReloot.getInstance().getDatabase(event.getWorld()) == null) {
            StructureReloot.getInstance().createDatabase(event.getWorld());
        }

        //Check only for newly created chunks
        if (!event.isNewChunk()) return;
        if (!event.getWorld().getEnvironment().equals(World.Environment.THE_END)) return;
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Entity e : event.getChunk().getEntities()) {
                    if (e instanceof ItemFrame) {
                        e.getPersistentDataContainer().set(markEntityKey, PersistentDataType.BYTE, (byte) 1);

                        if (StructureReloot.getInstance().isDebugMode())
                            StructureReloot.LOGGER.log(Level.INFO, "Marked Itemframe as LootItemFrame at: " + LootBlockValue.locationToLocationString(e.getLocation()) + " in World " + event.getWorld().getName());
                    }
                }
            }
        }.runTaskLater(StructureReloot.getInstance(), 2);

    }

    /*
        If itemframe get's damaged
     */
    @EventHandler
    public void onPlayerDamageItemFrame(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ItemFrame)) return;
        if (!event.getEntity().getPersistentDataContainer().has(markEntityKey, PersistentDataType.BYTE)) return;

        ItemFrame entity = ((ItemFrame) event.getEntity());
        if (!entity.getItem().getType().equals(Material.ELYTRA)) return; //Don't know what happened here
        LootEntityValue lev = new LootEntityValue(EntityType.ITEM_FRAME, entity.getLocation(), null, entity.getUniqueId());
        StructureReloot.getInstance().getDatabase(entity.getWorld()).addEntity(lev); // Save to database
        entity.getPersistentDataContainer().remove(markEntityKey); //IMPORTANT: Remove markerkey

        if (StructureReloot.getInstance().isDebugMode())
            StructureReloot.LOGGER.log(Level.INFO, "Added new ItemFrame to Database at location " + lev.getLocationString());
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        if (!(event.getEntity() instanceof ItemFrame)) return;
        if (!event.getEntity().getPersistentDataContainer().has(markEntityKey, PersistentDataType.BYTE)) return;

        ItemFrame entity = ((ItemFrame) event.getEntity());
        if (!entity.getItem().getType().equals(Material.ELYTRA)) return; //Don't know what happened here
        LootEntityValue lev = new LootEntityValue(EntityType.ITEM_FRAME, entity.getLocation(), null, entity.getUniqueId());
        StructureReloot.getInstance().getDatabase(entity.getWorld()).addEntity(lev); // Save to database
        entity.getPersistentDataContainer().remove(markEntityKey); //IMPORTANT: Remove markerkey

        if (StructureReloot.getInstance().isDebugMode())
            StructureReloot.LOGGER.log(Level.INFO, "Added new ItemFrame to Database at location " + lev.getLocationString());

    }

}
