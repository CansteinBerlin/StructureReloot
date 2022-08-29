package me.hasenzahn1.structurereloot.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;

public class BlockListener implements Listener {

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event){
        Location loc = event.getLootContext().getLocation();
    }

}
