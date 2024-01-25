package me.hasenzahn1.structurereloot.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
import me.hasenzahn1.structurereloot.database.WorldDatabase;
import org.bukkit.World;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RelootHelper {

    public static void relootMultipleBlocks(List<LootBlockValue> values, List<Runnable> callbacks) {
        StructureReloot.getInstance().getLootValueProcessor().addToProcessQueue(new LootValueProcessor.LootValueQueueElement(values, callbacks));
    }

    public static void relootMultipleEntities(List<LootEntityValue> values, List<Runnable> callbacks) {
        StructureReloot.getInstance().getLootValueProcessor().addToProcessQueue(new LootValueProcessor.LootValueQueueElement(values, callbacks));
    }

    public static void regenNEntities(World world, int amount, Runnable runnable) {
        //Get %amount% of random LootEntityValues to reloot
        List<LootEntityValue> levs = StructureReloot.getInstance().getDatabaseManager().getDatabase(world).getAllEntities();
        Collections.shuffle(levs);
        List<LootEntityValue> values = levs.stream().limit(Math.min(levs.size(), amount)).collect(Collectors.toList());

        //Reloot those entities
        RelootHelper.relootMultipleEntities(values, runnable != null ? List.of(runnable) : List.of());

        //Remove all relooted entities from the database.
        WorldDatabase database = StructureReloot.getInstance().getDatabaseManager().getDatabase(world);
        database.removeMultipleEntities(values);
    }

    public static void regenNBlocks(World world, int amount, Runnable runnable) {
        //Get %amount% of random LootBlockValues to reloot
        List<LootBlockValue> lbvs = StructureReloot.getInstance().getDatabaseManager().getDatabase(world).getAllBlocks();
        Collections.shuffle(lbvs);
        List<LootBlockValue> values = lbvs.stream().limit(Math.min(lbvs.size(), amount)).collect(Collectors.toList());

        //Reloot those blocks
        RelootHelper.relootMultipleBlocks(values, runnable != null ? List.of(runnable) : List.of());

        //Remove from the database
        WorldDatabase database = StructureReloot.getInstance().getDatabaseManager().getDatabase(world);
        database.removeMultipleBlocks(values);
    }

}

