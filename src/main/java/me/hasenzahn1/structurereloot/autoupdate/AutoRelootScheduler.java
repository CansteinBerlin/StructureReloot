package me.hasenzahn1.structurereloot.autoupdate;

import me.hasenzahn1.structurereloot.StructureReloot;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoRelootScheduler extends BukkitRunnable {

    @Override
    public void run() {
        StructureReloot.getInstance().relootElementsInWorld(false);
    }

}
