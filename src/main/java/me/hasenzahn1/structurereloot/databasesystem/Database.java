package me.hasenzahn1.structurereloot.databasesystem;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Database {

    JavaPlugin plugin;
    private String fileName;
    private Connection connection;
    private HashMap<Class<? extends Table>, Table> tables;


    public Database(JavaPlugin plugin, String fileName){
        this.plugin = plugin;
        this.fileName = fileName.replace(".db", "");
        tables = new HashMap<>();
    }

    public void init(){
        connection = getSQLConnection();

        for(Table table : tables.values()){
            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate(table.getCreationString());
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        close();
    }

    protected Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), fileName + ".db");
        if(!dataFolder.exists()){
            try {
                dataFolder.getParentFile().mkdirs();
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create database file: " + dataFolder.getAbsolutePath());
            }
        }
        try {
            if(connection != null && !connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", e);
        } catch (ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }


        return null;
    }

    public <T extends Table> void addTable(T table){
        tables.put(table.getClass(), table);
    }

    public <T extends Table> T getTable(Class<T> table){
        return (T) tables.get(table);
    }

    public Logger getLogger(){
        return plugin.getLogger();
    }

    public void close(){
        try {
            if(connection != null) connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
