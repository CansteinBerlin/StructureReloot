package me.hasenzahn1.structurereloot.listeners;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrushableBlock;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class BlockListener implements Listener {

    public static final NamespacedKey SAVED_LOOT_TABLE = new NamespacedKey(StructureReloot.getInstance(), "loot_table_save");

    /*
        Should not receive entityLootables as well as plugins calling
        LootTable.fillInventory(org.bukkit.inventory.Inventory, java.util.Random, LootContext).
     */
    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        if (event.getInventoryHolder() instanceof Entity) return;
        if (event.isPlugin()) return;

        // Do not Capture Broken Blocks
        Location loc = event.getLootContext().getLocation();
        if (loc.getBlock().getType() == Material.AIR) return;

        //Save to database
        LootTable lootTable = event.getLootTable();
        LootBlockValue lootBlockValue = new LootBlockValue(loc, lootTable);
        StructureReloot.getInstance().getDatabaseManager().getDatabase(event.getWorld()).addBlock(lootBlockValue);
    }

    /*
        Blocks can be broken using explosion
    */
    @EventHandler
    public void onExplosionBreakChest(EntityExplodeEvent event) {
        //Fetch all broken Loot blocks
        List<Block> lootBlocks = event.blockList()
                .stream()
                .filter(b -> b.getState() instanceof Lootable)
                .filter(b -> ((Lootable) b.getState()).getLootTable() != null)
                .toList();

        //Save Blocks to database
        for (Block b : lootBlocks) {
            if (b.getBlockData() instanceof Directional) handleDirectionalBlock(b);
            else handleNonDirectionalBlock(b);
        }
    }

    /*
        Blocks can be broken by player
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof Lootable)) return;
        if (event.getBlock().getState() instanceof BrushableBlock) return; // Ignore Brushable Block

        Block b = event.getBlock();
        if (b.getBlockData() instanceof Directional) handleDirectionalBlock(b);
        else handleNonDirectionalBlock(b);
    }

    private void handleDirectionalBlock(Block block) {
        Location loc = block.getLocation();
        LootTable lootTable = ((Lootable) block.getState()).getLootTable();
        if (lootTable == null) return;
        LootBlockValue lbv = new LootBlockValue(loc, lootTable, block.getType(), ((Directional) block.getBlockData()).getFacing());
        StructureReloot.getInstance().getDatabaseManager().getDatabase(loc.getWorld()).addBlock(lbv);
    }

    private void handleNonDirectionalBlock(Block block) {
        Location loc = block.getLocation();
        LootTable lootTable = ((Lootable) block.getState()).getLootTable();
        if (lootTable == null) return;
        LootBlockValue lbv = new LootBlockValue(loc, lootTable, block.getType(), BlockFace.UP);
        StructureReloot.getInstance().getDatabaseManager().getDatabase(loc.getWorld()).addBlock(lbv);
    }

    /*
    We have to save the lootTable of each suspicious sand/gravel as they are removed when brushing the block
     */
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (BlockState state : event.getChunk().getTileEntities()) {
            if (!(state instanceof BrushableBlock)) continue;
            LootTable table = ((BrushableBlock) state).getLootTable();
            if (table == null) continue;
            ((BrushableBlock) state).getPersistentDataContainer().set(SAVED_LOOT_TABLE, PersistentDataType.STRING, table.getKey() + "");
            state.update();
        }
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        //System.out.println(new ClassDebug(event));
        if (!(event.getBlockState() instanceof BrushableBlock)) return;
        if (!((BrushableBlock) event.getBlockState()).getPersistentDataContainer().has(SAVED_LOOT_TABLE)) return;

        //Create, Fetch and Save LootValue
        Location loc = event.getBlock().getLocation();
        NamespacedKey key = NamespacedKey.fromString(((BrushableBlock) event.getBlockState()).getPersistentDataContainer().get(SAVED_LOOT_TABLE, PersistentDataType.STRING));
        if (key == null) return;
        LootTable lootTable = Bukkit.getLootTable(key);
        LootBlockValue lbv = new LootBlockValue(loc, lootTable, event.getBlockState().getType(), BlockFace.UP);
        StructureReloot.getInstance().getDatabaseManager().getDatabase(loc.getWorld()).addBlock(lbv);
    }

    @EventHandler
    public void onBlockConvertToEntity(EntityChangeBlockEvent event) {
        if (!(event.getBlock().getState() instanceof BrushableBlock)) return;
        if (!(((BrushableBlock) event.getBlock().getState()).getPersistentDataContainer().has(SAVED_LOOT_TABLE)))
            return;

        //Create, Fetch and Save LootValue
        Location loc = event.getBlock().getLocation();
        NamespacedKey key = NamespacedKey.fromString(((BrushableBlock) event.getBlock().getState()).getPersistentDataContainer().get(SAVED_LOOT_TABLE, PersistentDataType.STRING));
        if (key == null) return;
        LootTable lootTable = Bukkit.getLootTable(key);
        LootBlockValue lbv = new LootBlockValue(loc, lootTable, event.getBlock().getState().getType(), BlockFace.UP);
        StructureReloot.getInstance().getDatabaseManager().getDatabase(loc.getWorld()).addBlock(lbv);
    }

}
