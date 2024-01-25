package me.hasenzahn1.structurereloot.general;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.config.LanguageConfig;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.database.LootEntityValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChangesPerDay {

    File file;
    DateTimeFormatter formatter;
    
    public ChangesPerDay() {
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyy hh:mm:ss");
    }

    public void markAddBlock(LootBlockValue value) {
        logToTextFile("Added new LootBlock at " + value.getLocationString() + " in " + value.getLocation().getWorld().getName() + "\n");
    }

    public void markAddEntity(LootEntityValue value) {
        logToTextFile("Added new LootEntity at " + value.getLocationString() + " in " + value.getLocation().getWorld().getName() + "\n");
    }

    public void markRemoveBlock(LootBlockValue value) {
        logToTextFile("Removed or relooted LootBlock at " + value.getLocationString() + " in " + value.getLocation().getWorld().getName() + "\n");
    }

    public void markRemoveEntity(LootEntityValue value) {
        logToTextFile("Removed or relooted LootEntity at " + value.getLocationString() + " in " + value.getLocation().getWorld().getName() + "\n");
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
