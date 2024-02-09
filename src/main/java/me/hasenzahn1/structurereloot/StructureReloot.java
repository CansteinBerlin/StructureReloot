package me.hasenzahn1.structurereloot;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import me.hasenzahn1.structurereloot.commands.RelootCommand;
import me.hasenzahn1.structurereloot.config.CustomConfig;
import me.hasenzahn1.structurereloot.config.LanguageConfig;
import me.hasenzahn1.structurereloot.config.UpdateConfig;
import me.hasenzahn1.structurereloot.database.DatabaseManager;
import me.hasenzahn1.structurereloot.general.AutoRelootScheduler;
import me.hasenzahn1.structurereloot.general.RelootActivityLogger;
import me.hasenzahn1.structurereloot.general.RelootSettings;
import me.hasenzahn1.structurereloot.listeners.BlockListener;
import me.hasenzahn1.structurereloot.listeners.EntityListener;
import me.hasenzahn1.structurereloot.reloot.LootValueProcessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public final class StructureReloot extends JavaPlugin {

    public static String PREFIX = "§b[§6StructureReloot§b] §r";
    private static StructureReloot instance;

    //Data Handling
    private boolean debugMode;
    private RelootActivityLogger relootActivityLogger;

    //Configs
    private CustomConfig defaultConfig;
    private LanguageConfig languageConfig;
    private UpdateConfig blockUpdateConfig;
    private UpdateConfig entityUpdateConfig;

    //Commands
    private PaperCommandManager paperCommandManager;

    //Database
    private List<World> disabledWorlds;
    private DatabaseManager databaseManager;

    //Automatic Relooting and processing of requested reloots
    private LootValueProcessor lootValueProcessor;
    private AutoRelootScheduler autoRelootScheduler;


    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(RelootSettings.class);

        //Static References
        instance = this;

        //Logging
        relootActivityLogger = new RelootActivityLogger(getLogger());

        //Config and Database
        initConfigs();
        databaseManager = new DatabaseManager("data");

        //Creation of tickable tasks
        lootValueProcessor = new LootValueProcessor();
        autoRelootScheduler = new AutoRelootScheduler();
        autoRelootScheduler.runTaskTimer(this, 20 * 5, 20 * 60);

        //Commands
        paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new RelootCommand());
        paperCommandManager.enableUnstableAPI("help");
        paperCommandManager.getCommandCompletions().registerCompletion("configName", c -> {
            return ImmutableList.of("lang", "config", "entityupdatesettings", "blockupdatesettings");
        });
        paperCommandManager.getCommandCompletions().registerCompletion("boolean", c -> {
            return ImmutableList.of("true", "false");
        });

        //Register Listeners
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
    }

    private void initConfigs() {
        languageConfig = new LanguageConfig(this);
        initDefaultConfig();

        blockUpdateConfig = new UpdateConfig("blockUpdateSettings.yml");
        entityUpdateConfig = new UpdateConfig("entityUpdateSettings.yml");
    }

    public void initDefaultConfig() {
        defaultConfig = new CustomConfig(this, "config.yml");
        PREFIX = ChatColor.translateAlternateColorCodes('&', defaultConfig.getConfig().getString("prefix", PREFIX));
        debugMode = defaultConfig.getConfig().getBoolean("debugMode", false);
        LootValueProcessor.CHANGE_AMOUNT = defaultConfig.getConfig().getInt("changesPerTick", 20);

        disabledWorlds = new ArrayList<>();
        for (String world : defaultConfig.getConfig().getStringList("world-blacklist")) {
            System.out.println(world);
            if (Bukkit.getWorld(world) != null) disabledWorlds.add(Bukkit.getWorld(world));
        }
        System.out.println(disabledWorlds);
    }

    @Override
    public void onDisable() {
        autoRelootScheduler.cancel();
    }

    public static StructureReloot getInstance() {
        return instance;
    }
}
