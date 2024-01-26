package me.hasenzahn1.structurereloot.general;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.reloot.RelootHelper;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * This class checks all worlds if they need to be relooted.
 */
public class AutoRelootScheduler extends BukkitRunnable {

    private final StructureReloot instance;

    public AutoRelootScheduler() {
        instance = StructureReloot.getInstance();
    }

    @Override
    public void run() {
        //Reloot Blocks
        List<World> neededBlockUpdateSettings = instance.getBlockUpdateConfig().getNeededUpdates();
        for (World world : neededBlockUpdateSettings) {
            RelootSettings settings = instance.getBlockUpdateConfig().getSettingsForWorld(world);
            RelootHelper.regenNBlocks(world, settings.getMaxRelootAmount(), null);
            settings.nextDate();
        }

        //Update BlockConfig
        if (!neededBlockUpdateSettings.isEmpty()) instance.getBlockUpdateConfig().update();


        //Reloot Entities
        List<World> neededEntityUpdateSettings = instance.getEntityUpdateConfig().getNeededUpdates();
        for (World world : neededEntityUpdateSettings) {
            RelootSettings settings = instance.getEntityUpdateConfig().getSettingsForWorld(world);
            RelootHelper.regenNEntities(world, settings.getMaxRelootAmount(), null);
            settings.nextDate();
        }

        //Update BlockConfig
        if (!neededEntityUpdateSettings.isEmpty()) instance.getEntityUpdateConfig().update();
    }
}
