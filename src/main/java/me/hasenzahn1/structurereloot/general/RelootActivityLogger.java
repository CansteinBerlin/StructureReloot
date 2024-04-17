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

    /**
     * Logs the adding of a block to the database to the log file/console
     *
     * @param value
     */
    public void logAddBlock(LootBlockValue value) {
        log(Level.OFF, "Added new LootBlock at " + value.getLocationString() + " in " + value.getLocation().getWorld().getName());
    }

    /**
     * Logs the adding of an entity to the database to the log file/console
     *
     * @param value
     */
    public void logAddEntity(LootEntityValue value) {
        log(Level.OFF, "Added new LootEntity at " + value.getLocationString() + " in " + value.getLocation().getWorld().getName());
    }

    /**
     * Logs the removal of a block from a database to the log file/console
     *
     * @param value
     */
    public void logRemoveBlock(LootBlockValue value) {
        log(Level.OFF, "Removed or relooted LootBlock at " + value.getLocationString() + " in " + value.getLocation().getWorld().getName());
    }

    /**
     * Logs the removal of an entity from a database to the log file/console
     *
     * @param value
     */
    public void logRemoveEntity(LootEntityValue value) {
        log(Level.OFF, "Removed or relooted LootEntity at " + value.getLocationString() + " in " + value.getLocation().getWorld().getName());
    }

    /**
     * Logs the creation of a new world database to the log file/console
     *
     * @param world
     */
    public void logNewWorld(World world) {
        log(Level.INFO, "Found new world with name: " + world.getName());
    }

    /**
     * General log method to the text file and the log file
     *
     * @param level The Log level. Level.OFF represents the debug level, and is sent to the textfile but only to the console if the plugin is in debug mode
     */
    public void log(Level level, String message) {
        logToTextFile("[" + ((level != Level.OFF) ? level.getName() : "DEBUG") + "]: " + message + "\n");
        if (level != Level.OFF || StructureReloot.getInstance().isDebugMode()) {
            logger.log(level != Level.OFF ? level : Level.INFO, message);
        }
    }

    /**
     * Creates a new log file if not exists
     */
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

    /**
     * Logs a text to the log file
     *
     * @param text
     */
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
