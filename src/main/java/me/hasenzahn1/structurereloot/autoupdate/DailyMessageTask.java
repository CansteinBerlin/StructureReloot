package me.hasenzahn1.structurereloot.autoupdate;

import me.hasenzahn1.structurereloot.StructureReloot;

import java.util.TimerTask;

public class DailyMessageTask extends TimerTask {

    private final ChangesPerDay changes;

    public DailyMessageTask(){
        changes = new ChangesPerDay();
    }

    @Override
    public void run() {
        StructureReloot.LOGGER.info(changes.toString());
        changes.clear();
    }

    public ChangesPerDay getChanges() {
        return changes;
    }
}
