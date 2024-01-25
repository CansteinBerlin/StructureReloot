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
        StructureReloot.getInstance().getLootValueProcessor().addElement(new LootValueProcessor.LootValueQueueElement(values, callbacks));
    }

    public static void relootMultipleEntities(List<LootEntityValue> values, List<Runnable> callbacks) {
        StructureReloot.getInstance().getLootValueProcessor().addElement(new LootValueProcessor.LootValueQueueElement(values, callbacks));
    }

    public static void regenNEntities(World world, int amount, Runnable runnable) {
        List<LootEntityValue> levs = StructureReloot.getInstance().getDatabaseManager().getDatabase(world).getAllEntities();
        Collections.shuffle(levs);
        List<LootEntityValue> values = levs.stream().limit(Math.min(levs.size(), amount)).collect(Collectors.toList());

        WorldDatabase database = StructureReloot.getInstance().getDatabaseManager().getDatabase(world);
        database.setCacheRemove(true);
        RelootHelper.relootMultipleEntities(values, runnable != null ? List.of(runnable) : List.of());
        database.removeMultipleEntities(values);
        database.setCacheRemove(false);
    }

    public static void regenNBlocks(World world, int amount, Runnable runnable) {
        List<LootBlockValue> lbvs = StructureReloot.getInstance().getDatabaseManager().getDatabase(world).getAllBlocks();
        Collections.shuffle(lbvs);
        List<LootBlockValue> values = lbvs.stream().limit(Math.min(lbvs.size(), amount)).collect(Collectors.toList());

        WorldDatabase database = StructureReloot.getInstance().getDatabaseManager().getDatabase(world);
        database.setCacheRemove(true);
        RelootHelper.relootMultipleBlocks(values, runnable != null ? List.of(runnable) : List.of());
        database.removeMultipleBlocks(values);
        database.setCacheRemove(false);
    }

}

