package me.hasenzahn1.structurereloot.reloot;

import lombok.Getter;
import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.LootValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Getter
public class LootValueProcessor {

    public static int CHANGE_AMOUNT = 10;

    private final ArrayList<LootValueQueueElement> elements;
    private BukkitTask task;

    public LootValueProcessor() {
        elements = new ArrayList<>();
    }

    public void addToProcessQueue(LootValueQueueElement element) {
        this.elements.add(element);
        if (!elements.isEmpty() && task == null) {
            startProcessingTask();
        }
    }

    private void startProcessingTask() {
        task = new BukkitRunnable() {
            @Override
            public void run() {

                if (elements.isEmpty()) {
                    cancel();
                    task = null;
                    return;
                }

                LootValueQueueElement element = elements.get(0);
                for (int i = 0; i < Math.min(CHANGE_AMOUNT, element.values.size()); i++) {
                    LootValue value = element.values.poll();
                    if (value == null) continue;

                    //Reloot Element
                    value.reloot();
                }

                //Check if element is finished
                if (element.isFinished()) {
                    element.notifyCallbacks();
                    elements.remove(0);
                }

            }
        }.runTaskTimer(StructureReloot.getInstance(), 0, 1);

    }


    @Getter
    public static class LootValueQueueElement {

        private final Queue<? extends LootValue> values;
        private final List<Runnable> finishCallbacks;

        public LootValueQueueElement(List<? extends LootValue> values, List<Runnable> finishCallbacks) {
            this.values = new LinkedList<>(values);
            this.finishCallbacks = finishCallbacks;
        }

        public boolean isFinished() {
            return values.isEmpty();
        }

        public void notifyCallbacks() {
            for (Runnable runnable : finishCallbacks) {
                runnable.run();
            }
        }
    }
}
