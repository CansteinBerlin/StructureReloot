package me.hasenzahn1.structurereloot.database.tables;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
import me.hasenzahn1.structurereloot.databasesystem.Database;
import me.hasenzahn1.structurereloot.databasesystem.Table;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EntityTable extends Table {

    private final World world;

    public EntityTable(Database database, World world) {
        super("entities", database);
        this.world = world;
    }

    //Creation
    @Override
    public String getCreationString() {
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                "location varchar(27) PRIMARY KEY," +
                "lootTable varchar(57)," +
                "uuid varchar(36)," +
                "entityType varchar(20) NOT NULL" +
                ");";
    }

    //Get Values
    public LootEntityValue getEntity(Location loc) {
        Connection con = getConnection();
        LootEntityValue value = null;
        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM " + getTableName() + " WHERE location=?")) {
            statement.setString(1, LootBlockValue.locationToLocationString(loc));
            ResultSet set = statement.executeQuery();

            if (set.next()) {
                String entityType = set.getString("entityType");
                String location = set.getString("location");
                NamespacedKey lootTable = NamespacedKey.fromString(set.getString("lootTable"));
                String uuid = set.getString("uuid");

                value = new LootEntityValue(world, entityType, location, lootTable, uuid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        close(con);
        return value;
    }

    public ArrayList<LootEntityValue> getAllEntities() {
        Connection con = getConnection();
        ArrayList<LootEntityValue> values = new ArrayList<>();
        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM " + getTableName())) {
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                String entityType = set.getString("entityType");
                String location = set.getString("location");
                NamespacedKey lootTable = NamespacedKey.fromString(set.getString("lootTable"));
                String uuid = set.getString("uuid");

                values.add(new LootEntityValue(world, entityType, location, lootTable, uuid));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        close(con);
        return values;
    }


    //Add Values
    public void addEntity(LootEntityValue value) {
        Connection con = getConnection();
        try (PreparedStatement statement = con.prepareStatement("INSERT OR REPLACE INTO " + getTableName() + " (location, lootTable, uuid, entityType) VALUES(?,?,?,?)")) {
            statement.setString(1, value.getLocationString());
            statement.setString(2, value.getStringLootTable());
            statement.setString(3, value.getUUIDString());
            statement.setString(4, value.getEntityString());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Update Changes per day
        StructureReloot.getInstance().getRelootActivityLogger().logAddEntity(value);
        close(con);
    }

    public void addMultipleEntities(List<LootEntityValue> values) {
        if (values.size() > 1000) {
            _addMultipleEntities(values.subList(0, 999));
            addMultipleEntities(values.subList(1000, values.size()));
        } else {
            _addMultipleEntities(values);
        }
        //Update Changes per day
        for (LootEntityValue value : values)
            StructureReloot.getInstance().getRelootActivityLogger().logAddEntity(value);
    }

    private void _addMultipleEntities(List<LootEntityValue> values) {
        if (values.isEmpty()) return;
        Connection con = getConnection();

        //Create sql string
        StringBuilder sqlString = new StringBuilder("INSERT OR REPLACE INTO " + getTableName() + " (location, lootTable, uuid, entityType) VALUES ");
        for (LootEntityValue val : values) {
            sqlString.append("('");
            sqlString.append(val.getLocationString()).append("','");
            sqlString.append(val.getStringLootTable()).append("','");
            sqlString.append(val.getUUIDString()).append("','");
            sqlString.append(val.getEntityString()).append("'),");
        }
        sqlString.deleteCharAt(sqlString.length() - 1);

        //execute Statement
        try (PreparedStatement statement = con.prepareStatement(sqlString.toString())) {
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        close(con);
    }


    //Remove Values
    public void removeEntity(LootEntityValue value) {
        Connection con = getConnection();
        try (PreparedStatement statement = con.prepareStatement("DELETE FROM " + getTableName() + " WHERE location=?")) {
            statement.setString(1, value.getLocationString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Update Changes per day
        StructureReloot.getInstance().getRelootActivityLogger().logRemoveEntity(value);
        close(con);
    }

    public void removeMultipleEntities(List<LootEntityValue> values) {
        if (values.isEmpty()) return;
        Connection con = getConnection();

        //Create sql String
        StringBuilder sqlString = new StringBuilder("DELETE FROM ").append(getTableName()).append(" WHERE location IN ('").append(values.get(0).getLocationString()).append("'");
        for (int i = 1; i < values.size(); i++) {
            sqlString.append(", '").append(values.get(i).getLocationString()).append("'");
        }
        sqlString.append(")");

        //Execute Statement
        try (PreparedStatement statement = con.prepareStatement(sqlString.toString())) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Update Changes per day
        for (LootEntityValue value : values)
            StructureReloot.getInstance().getRelootActivityLogger().logRemoveEntity(value);
        close(con);
    }


}
