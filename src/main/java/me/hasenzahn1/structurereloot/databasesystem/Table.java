package me.hasenzahn1.structurereloot.databasesystem;


import java.sql.Connection;

public abstract class Table {

    private final String tableName;
    private final Database database;

    public Table(String tableName, Database database) {
        this.tableName = tableName;
        this.database = database;
    }

    public abstract String getCreationString();

    public String getTableName() {
        return tableName;
    }

    protected Connection getConnection(){
        return database.getSQLConnection();
    }
}
