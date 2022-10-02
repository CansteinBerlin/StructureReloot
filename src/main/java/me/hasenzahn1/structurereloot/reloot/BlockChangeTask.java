package me.hasenzahn1.structurereloot.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class BlockChangeTask extends BukkitRunnable{

    public static int BLOCK_CHANGE_AMOUNT = 10;

    private final ArrayList<LootBlockValue> blocksToChange;
    private final ArrayList<Runnable> finishedTaskRunnables;

    public BlockChangeTask(){
        blocksToChange = new ArrayList<>();
        finishedTaskRunnables = new ArrayList<>();
        runTaskTimer(StructureReloot.getInstance(), 0, 1);
    }


    @Override
    public void run() {
        for(int i = Math.min(blocksToChange.size(), BLOCK_CHANGE_AMOUNT) - 1; i >= 0; i--){
            blocksToChange.get(i).reloot();
            blocksToChange.remove(i);
        }

        if(blocksToChange.size() == 0){

            for(Runnable r : finishedTaskRunnables){
                r.run();
            }
            finishedTaskRunnables.clear();
        }
    }

    public void changeBlock(LootBlockValue value){
        blocksToChange.add(value);
    }

    public void addCallback(Runnable runnable){
        if(runnable == null) return;
        finishedTaskRunnables.add(runnable);
    }

    public void changeBlocks(List<LootBlockValue> values){
        blocksToChange.addAll(values);
    }


}
