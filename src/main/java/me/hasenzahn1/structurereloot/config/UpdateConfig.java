package me.hasenzahn1.structurereloot.config;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.autoupdate.RelootSettings;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class UpdateConfig extends CustomConfig{

    private final HashMap<World, RelootSettings> settings;

    public UpdateConfig(String name) {
        super(StructureReloot.getInstance(), name);

        settings = new HashMap<>();
        FileConfiguration config = getConfig();

        for(World world : Bukkit.getWorlds()){
            //Load if exists
            if(config.contains(world.getName())){
                settings.put(world, config.getObject(world.getName(), RelootSettings.class));
                continue;
            }

            //Otherwise, create new
            settings.put(world, new RelootSettings(true, -1, "1h"));
        }

        if(isNew) update();
    }

    public RelootSettings getSettingsForWorld(World world){
        return settings.getOrDefault(world, null);
    }

    public void update(){
        FileConfiguration config = getConfig();
        for(Map.Entry<World, RelootSettings> entry : settings.entrySet()){
            config.set(entry.getKey().getName(), entry.getValue());
        }
        saveConfig();
    }
}
