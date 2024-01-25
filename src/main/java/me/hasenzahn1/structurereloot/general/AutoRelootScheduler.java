package me.hasenzahn1.structurereloot.general;

import me.hasenzahn1.structurereloot.StructureReloot;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoRelootScheduler extends BukkitRunnable {

    @Override
    public void run() {
        StructureReloot.getInstance().relootElementsInWorld(false);
    }

}
