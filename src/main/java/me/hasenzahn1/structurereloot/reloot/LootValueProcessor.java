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

    //The amount of blocks that should at max be changed each tick. This value is set from the Main Plugin class
    public static int CHANGE_AMOUNT = 10;

    private final ArrayList<LootValueQueueElement> elements;
    private BukkitTask task;

    public LootValueProcessor() {
        elements = new ArrayList<>();
    }

    /**
     * Add a new queueElement to the processing queue
     *
     * @param element The element that should be processed
     */
    public void addToProcessQueue(LootValueQueueElement element) {
        if (elements.isEmpty() && task == null) {
            startProcessingTask();
        }
        this.elements.add(element);
    }

    private void startProcessingTask() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                //If the queue is empty terminate!
                if (elements.isEmpty()) {
                    cancel();
                    task = null;
                    return;
                }

                //Get the first queueElement and reloot n of it's values
                LootValueQueueElement element = elements.get(0);
                for (int i = 0; i < Math.min(CHANGE_AMOUNT, element.values.size()); i++) {
                    LootValue value = element.values.poll();
                    if (value == null) continue;
                    value.reloot();
                }

                //Check if element is finished, if so remove from the elements
                if (element.isFinished()) {
                    element.notifyCallbacks();
                    elements.remove(0);
                }

            }
        }.runTaskTimer(StructureReloot.getInstance(), 1, 1);

    }

    /**
     * This class represents a list of LootValues that should be relooted. Callbacks can be provided to receive an update if the task is finished.
     */
    @Getter
    public static class LootValueQueueElement {

        private final Queue<? extends LootValue> values;
        private final List<Runnable> finishCallbacks;

        public LootValueQueueElement(List<? extends LootValue> values, List<Runnable> finishCallbacks) {
            this.values = new LinkedList<>(values);
            this.finishCallbacks = finishCallbacks;
        }

        private boolean isFinished() {
            return values.isEmpty();
        }

        private void notifyCallbacks() {
            for (Runnable runnable : finishCallbacks) {
                runnable.run();
            }
        }
    }
}
