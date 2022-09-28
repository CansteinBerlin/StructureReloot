package me.hasenzahn1.structurereloot.autoupdate;

import me.hasenzahn1.structurereloot.StructureReloot;
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

    public ChangesPerDay(){
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
    }

    private void create(){
        DateTimeFormatter f = DateTimeFormatter.ISO_LOCAL_DATE;
        String fileTitle = f.format(LocalDateTime.now()) + ".txt";
        file = new File(StructureReloot.getInstance().getDataFolder(), "logs/" + fileTitle);
        file.getParentFile().mkdirs();
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void markAddBlock(LootBlockValue value){
        create();
        try {
            Files.writeString(
                    Paths.get(file.toURI()),
                    "[" + formatter.format(LocalDateTime.now()) + "]: Added new LootBlock at " + value.getLocationString() + " in world " + value.getLoc().getWorld().getName() + "\n",
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void markAddEntity(LootEntityValue value){
        create();
        try {
            Files.writeString(
                    Paths.get(file.toURI()),
                    "[" + formatter.format(LocalDateTime.now()) + "]: Added new LootEntity at " + value.getLocationString() + " in world " + value.getLocation().getWorld().getName() + "\n",
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void markRemoveBlock(LootBlockValue value){
        create();
        try {
            Files.writeString(
                    Paths.get(file.toURI()),
                    "[" + formatter.format(LocalDateTime.now()) + "]: Removed LootBlock at " + value.getLocationString() + " in world " + value.getLoc().getWorld().getName() + "\n",
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void markRemoveEntity(LootEntityValue value){
        create();
        try {
            Files.writeString(
                    Paths.get(file.toURI()),
                    "[" + formatter.format(LocalDateTime.now()) + "]: Removed LootEntity at " + value.getLocationString() + " in world " + value.getLocation().getWorld().getName() + "\n",
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear(){
    }

}
