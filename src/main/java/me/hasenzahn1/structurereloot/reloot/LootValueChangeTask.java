package me.hasenzahn1.structurereloot.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.LootValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class LootValueChangeTask extends BukkitRunnable {

    public static int CHANGE_AMOUNT = 10;

    private ArrayList<LootValue> values;
    private ArrayList<Runnable> finishCallbacks;

    public LootValueChangeTask() {
        values = new ArrayList<>();
        finishCallbacks = new ArrayList<>();
        runTaskTimer(StructureReloot.getInstance(), 0, 1);
    }

    @Override
    public void run() {
        for (int i = Math.min(values.size(), CHANGE_AMOUNT) - 1; i >= 0; i--) {
            values.get(i).reloot();
            values.remove(i);
        }

        if (values.size() == 0) {

            for (Runnable r : finishCallbacks) {
                r.run();
            }
            finishCallbacks.clear();
        }
    }

    public void addValue(LootValue value) {
        values.add(value);
    }

    public void addValues(List<? extends LootValue> vs) {
        values.addAll(vs);
    }

    public void addCallback(Runnable runnable) {
        if (runnable == null) return;
        finishCallbacks.add(runnable);
    }
}
