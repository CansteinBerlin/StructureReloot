package me.hasenzahn1.structurereloot.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class EntityChangeTask extends BukkitRunnable {

    public static int ENTITY_CHANGE_AMOUNT = 10;

    private final ArrayList<LootEntityValue> entitiesToChange;
    private final ArrayList<Runnable> finishedTaskRunnables;
    private boolean running;

    public EntityChangeTask(){
        entitiesToChange = new ArrayList<>();
        finishedTaskRunnables = new ArrayList<>();
        runTaskTimer(StructureReloot.getInstance(), 0, 1);
    }

    @Override
    public void run() {
        for(int i = Math.min(entitiesToChange.size(), ENTITY_CHANGE_AMOUNT) - 1; i >= 0; i--){
            RelootHelper.relootOneEntity(entitiesToChange.get(i));
            entitiesToChange.remove(i);
        }

        if(entitiesToChange.size() == 0){
            running = false;

            for(Runnable r : finishedTaskRunnables){
                r.run();
            }
            finishedTaskRunnables.clear();
        }
    }

    public void changeEntitie(LootEntityValue value){
        entitiesToChange.add(value);
    }

    public void addCallback(Runnable runnable){
        if(runnable == null) return;
        finishedTaskRunnables.add(runnable);
    }

    public void changeEntities(List<LootEntityValue> values){
        entitiesToChange.addAll(values);
    }
}
