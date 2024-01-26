package me.hasenzahn1.structurereloot.listeners;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
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
        Used to detect chest minecart opening
     */
    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        if (event.isPlugin()) return; // Don't capture loot generated from plugin
        if (!(event.getInventoryHolder() instanceof StorageMinecart minecart)) return;

        //Create Storage entry and save to the database
        LootEntityValue lev = new LootEntityValue(minecart.getType(), minecart.getLocation(), event.getLootTable(), minecart.getUniqueId());
        StructureReloot.getInstance().getDatabaseManager().getDatabase(event.getWorld()).addEntity(lev);
    }


    /*
        If a chunk that has not been loaded is generated with an Item frame. The itemframe is marked for later use.
     */
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!event.isNewChunk()) return; //Check only for newly created chunks
        if (!event.getWorld().getEnvironment().equals(World.Environment.THE_END)) return;

        //Wait for the chunk to load. Then mark the entity
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity e : event.getChunk().getEntities()) {
                    if (e instanceof ItemFrame) {
                        e.getPersistentDataContainer().set(markEntityKey, PersistentDataType.BYTE, (byte) 1);

                        StructureReloot.getInstance().getLogger().log(Level.OFF, "Marked Itemframe as LootItemFrame at: " + LootBlockValue.locationToLocationString(e.getLocation()) + " in World " + event.getWorld().getName());
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
        if (!(event.getEntity() instanceof ItemFrame entity)) return;
        handleBrokenItemFrame(entity);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        if (!(event.getEntity() instanceof ItemFrame entity)) return;
        handleBrokenItemFrame(entity);
    }

    private void handleBrokenItemFrame(ItemFrame itemFrame) {
        if (!itemFrame.getPersistentDataContainer().has(markEntityKey, PersistentDataType.BYTE)) // Not marked. Ignore!
            return;

        //Item generated not with elytra, Ignore!
        if (!itemFrame.getItem().getType().equals(Material.ELYTRA)) return;

        //Save to database
        LootEntityValue lev = new LootEntityValue(EntityType.ITEM_FRAME, itemFrame.getLocation(), null, itemFrame.getUniqueId());
        StructureReloot.getInstance().getDatabaseManager().getDatabase(itemFrame.getWorld()).addEntity(lev); // Save to database
        itemFrame.getPersistentDataContainer().remove(markEntityKey); //IMPORTANT: Remove markerkey
    }

}
