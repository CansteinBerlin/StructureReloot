package me.hasenzahn1.structurereloot.general;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RelootActivityLogger {

    private File file;
    private final DateTimeFormatter formatter;
    private final Logger logger;

    public RelootActivityLogger(Logger logger) {
        this.logger = logger;
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyy hh:mm:ss");
    }

    public void logAddBlock(LootBlockValue value) {
        log(Level.OFF, "Added new LootBlock at " + value.getLocationString() + " in " + value.getLocation().getWorld().getName());
    }

    public void logAddEntity(LootEntityValue value) {
        log(Level.OFF, "Added new LootEntity at " + value.getLocationString() + " in " + value.getLocation().getWorld().getName());
    }

    public void logRemoveBlock(LootBlockValue value) {
        log(Level.OFF, "Removed or relooted LootBlock at " + value.getLocationString() + " in " + value.getLocation().getWorld().getName());
    }

    public void logRemoveEntity(LootEntityValue value) {
        log(Level.OFF, "Removed or relooted LootEntity at " + value.getLocationString() + " in " + value.getLocation().getWorld().getName());
    }

    public void logNewWorld(World world) {
        log(Level.INFO, "Found new world with name: " + world.getName());
    }

    public void log(Level level, String message) {
        logToTextFile("[" + ((level != Level.OFF) ? level.getName() : "DEBUG") + "]: " + message + "\n");
        if (level != Level.OFF || StructureReloot.getInstance().isDebugMode()) {
            logger.log(level != Level.OFF ? level : Level.INFO, message);
        }
    }

    private void create() {
        DateTimeFormatter f = DateTimeFormatter.ISO_LOCAL_DATE;
        String fileTitle = f.format(LocalDateTime.now()) + ".txt";
        file = new File(StructureReloot.getInstance().getDataFolder(), "logs/" + fileTitle);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void logToTextFile(String text) {
        create();
        try {
            Files.writeString(
                    Paths.get(file.toURI()),
                    "[" + formatter.format(LocalDateTime.now()) + "]: " + text,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
