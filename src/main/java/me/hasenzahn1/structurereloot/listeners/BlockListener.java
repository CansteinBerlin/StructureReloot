package me.hasenzahn1.structurereloot.listeners;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BlockListener implements Listener {

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event){
        Location loc = event.getLootContext().getLocation();
        if(loc.getBlock().getType() == Material.AIR) return; // Do not Capture Broken Blocks
        LootTable lootTable = event.getLootTable();
        LootBlockValue lootBlockValue = new LootBlockValue(loc, lootTable);
        StructureReloot.getInstance().getDatabase(event.getWorld()).addBlock(lootBlockValue).close();
        if(StructureReloot.getInstance().isDebugMode()) StructureReloot.LOGGER.log(Level.INFO, "Added new LootBlock to Database at location " + LootBlockValue.locationToLocationString(loc) + " with lootTable: " + lootTable);
    }

    /*
        Blocks can be broken using explosion
    */
    @EventHandler
    public void onExplosionBreakChest(EntityExplodeEvent event){
        List<Block> lootBlocks = event.blockList().stream().filter(b -> b.getState() instanceof Lootable).filter(b -> ((Lootable)b.getState()).getLootTable() != null).collect(Collectors.toList());
        for(Block b : lootBlocks){
            Location loc = b.getLocation();
            LootTable lootTable = ((Lootable)b.getState()).getLootTable();
            LootBlockValue lbv = new LootBlockValue(loc, lootTable, b.getType(), ((Directional)b.getBlockData()).getFacing());
            StructureReloot.getInstance().getDatabase(loc.getWorld()).addBlock(lbv).close();
            if(StructureReloot.getInstance().isDebugMode()) StructureReloot.LOGGER.log(Level.INFO, "Added new LootBlock to Database at location " + LootBlockValue.locationToLocationString(loc) + " with lootTable: §6" + lootTable);
        }
    }

    /*
        Blocks can be broken by player
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Block b = event.getBlock();
        if(!(b.getState() instanceof Lootable)) return;
        LootTable lootTable = ((Lootable)b.getState()).getLootTable();
        if(lootTable == null) return;
        Location loc = b.getLocation();
        LootBlockValue lbv = new LootBlockValue(loc, lootTable, b.getType(), ((Directional)b.getBlockData()).getFacing());
        StructureReloot.getInstance().getDatabase(loc.getWorld()).addBlock(lbv).close();
        if(StructureReloot.getInstance().isDebugMode()) StructureReloot.LOGGER.log(Level.INFO, "Added new LootBlock to Database at location " + LootBlockValue.locationToLocationString(loc) + " with lootTable: " + lootTable);
    }

}
