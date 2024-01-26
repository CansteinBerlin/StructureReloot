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

    /**
     * This listens to loot generation. If a block generates it's loot it should be added to database.
     * This listener is not supposed to listen to loot generation for broken blocks as some necessary information is missing
     *
     * @param event
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

    /**
     * Adds all relootable blocks that are broke due to an explosion to the database
     *
     * @param event
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

    /**
     * Adds all relootable blocks that are broken from players to the database
     *
     * @param event
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof Lootable)) return;
        if (event.getBlock().getState() instanceof BrushableBlock) return; // Ignore Brushable Block

        Block b = event.getBlock();
        if (b.getBlockData() instanceof Directional) handleDirectionalBlock(b);
        else handleNonDirectionalBlock(b);
    }


    /**
     * This listener adds a new nbt tag to all Brushable blocks (Suspicious Sand/Gravel) that additionally saves the lootTable of the block.
     * This is necessary as brushing a brushable block erases the lootTable data and replaces it with the item that is received.
     *
     * @param event
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

    /**
     * This event is used when a player finished to brush a brushable block. Then the block should be added to the database
     *
     * @param event
     */
    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        //System.out.println(new ClassDebug(event));
        if (!(event.getBlockState() instanceof BrushableBlock)) return;

        //Handle the block
        handleBrushBlock(event.getBlockState().getBlock());
    }

    /**
     * If a brushable block is converted to a falling entity, as the block below it is removed the block should als be added to the database.
     * Brushable blocks will be removed when falling.
     *
     * @param event
     */
    @EventHandler
    public void onBlockConvertToEntity(EntityChangeBlockEvent event) {
        if (!(event.getBlock().getState() instanceof BrushableBlock)) return;

        //Handle the Block
        handleBrushBlock(event.getBlock());
    }


    //============ Handler Methods ============//

    /**
     * Performs all necessary checks and add a brushable block to the database
     *
     * @param block The Brushable block to add
     */
    private void handleBrushBlock(Block block) {
        if (!(((BrushableBlock) block.getState()).getPersistentDataContainer().has(SAVED_LOOT_TABLE)))
            return;

        //Create, Fetch and Save LootValue
        Location loc = block.getLocation();
        NamespacedKey key = NamespacedKey.fromString(((BrushableBlock) block.getState()).getPersistentDataContainer().get(SAVED_LOOT_TABLE, PersistentDataType.STRING));
        if (key == null) return;
        LootTable lootTable = Bukkit.getLootTable(key);
        LootBlockValue lbv = new LootBlockValue(loc, lootTable, block.getState().getType(), BlockFace.UP);
        StructureReloot.getInstance().getDatabaseManager().getDatabase(loc.getWorld()).addBlock(lbv);
    }

    /**
     * Adds a directional block like a chest, or a dispenser to the database. These have to be handled differently as non-directional blocks
     *
     * @param block The directional block to add
     */
    private void handleDirectionalBlock(Block block) {
        Location loc = block.getLocation();
        LootTable lootTable = ((Lootable) block.getState()).getLootTable();
        if (lootTable == null) return;
        LootBlockValue lbv = new LootBlockValue(loc, lootTable, block.getType(), ((Directional) block.getBlockData()).getFacing());
        StructureReloot.getInstance().getDatabaseManager().getDatabase(loc.getWorld()).addBlock(lbv);
    }

    /**
     * Adds a non-directional block to the database. An example of this is a decorated pot. The block is added with a default facing value of UP
     *
     * @param block The non-directional block to add
     */
    private void handleNonDirectionalBlock(Block block) {
        Location loc = block.getLocation();
        LootTable lootTable = ((Lootable) block.getState()).getLootTable();
        if (lootTable == null) return;
        LootBlockValue lbv = new LootBlockValue(loc, lootTable, block.getType(), BlockFace.UP);
        StructureReloot.getInstance().getDatabaseManager().getDatabase(loc.getWorld()).addBlock(lbv);
    }

}
