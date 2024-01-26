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

public class BlockTable extends Table {

    private final World world;

    public BlockTable(Database database, World world) {
        super("blocks", database);
        this.world = world;
    }

    //Creation
    @Override
    public String getCreationString() {
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                "location varchar(27) PRIMARY KEY," +
                "lootTable varchar(57) NOT NULL," +
                "block varchar(40) NOT NULL," +
                "facing varchar(6) NOT NULL)" +
                ";";
    }

    //get Value
    public LootBlockValue getBlock(Location loc) {
        Connection con = getConnection();
        LootBlockValue value = null;
        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM " + getTableName() + " WHERE location=?")) {
            statement.setString(1, LootEntityValue.locationToLocationString(loc));
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                String location = set.getString("location");
                NamespacedKey lootTable = NamespacedKey.fromString(set.getString("lootTable"));
                String block = set.getString("block");
                String facing = set.getString("facing");
                value = new LootBlockValue(world, location, lootTable, block, facing);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        close(con);
        return value;
    }

    public ArrayList<LootBlockValue> getAllBlocks() {
        Connection con = getConnection();
        ArrayList<LootBlockValue> values = new ArrayList<>();
        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM " + getTableName())) {
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                String location = set.getString("location");
                NamespacedKey lootTable = NamespacedKey.fromString(set.getString("lootTable"));
                String block = set.getString("block");
                String facing = set.getString("facing");
                values.add(new LootBlockValue(world, location, lootTable, block, facing));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        close(con);
        return values;
    }

    //add Value
    public void addBlock(LootBlockValue value) {
        Connection con = getConnection();
        try (PreparedStatement statement = con.prepareStatement("INSERT OR REPLACE INTO " + getTableName() + " (location, lootTable, block, facing) VALUES(?,?,?,?)")) {
            statement.setString(1, value.getLocationString());
            statement.setString(2, value.getStringLootTable());
            statement.setString(3, value.getBlockMaterialString());
            statement.setString(4, value.getFacingString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Update Changes per day
        StructureReloot.getInstance().getRelootActivityLogger().logAddBlock(value);
        close(con);
    }

    public void addMultipleBlocks(List<LootBlockValue> values) {
        if (values.size() > 1000) {
            _addMultipleBlocks(values.subList(0, 999));
            addMultipleBlocks(values.subList(1000, values.size()));
        } else {
            _addMultipleBlocks(values);
        }
        //Update Changes per day
        for (LootBlockValue value : values) StructureReloot.getInstance().getRelootActivityLogger().logAddBlock(value);
    }

    private void _addMultipleBlocks(List<LootBlockValue> values) {
        if (values.isEmpty()) return;

        Connection con = getConnection();
        StringBuilder sqlString = new StringBuilder("INSERT OR REPLACE INTO " + getTableName() + " (location, lootTable, block, facing) VALUES ");
        for (LootBlockValue val : values) {
            sqlString.append("('");
            sqlString.append(val.getLocationString()).append("','");
            sqlString.append(val.getStringLootTable()).append("','");
            sqlString.append(val.getBlockMaterialString()).append("','");
            sqlString.append(val.getFacingString()).append("'),");
        }
        sqlString.deleteCharAt(sqlString.length() - 1);
        //Bukkit.broadcastMessage(sqlString.toString());
        try (PreparedStatement statement = con.prepareStatement(
                sqlString.toString()
        )) {
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        close(con);
    }

    // remove Value
    public void removeBlock(LootBlockValue value) {
        Connection con = getConnection();
        try (PreparedStatement statement = con.prepareStatement("DELETE FROM " + getTableName() + " WHERE location=?")) {
            statement.setString(1, value.getLocationString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Update Changes per day
        StructureReloot.getInstance().getRelootActivityLogger().logRemoveBlock(value);
        close(con);
    }

    public void removeMultipleBlocks(List<LootBlockValue> values) {
        if (values.isEmpty()) return;
        Connection con = getConnection();

        //Create statement
        StringBuilder sqlString = new StringBuilder("DELETE FROM ").append(getTableName()).append(" WHERE location IN ('").append(values.get(0).getLocationString()).append("'");
        for (int i = 1; i < values.size(); i++) {
            sqlString.append(", '").append(values.get(i).getLocationString()).append("'");
        }
        sqlString.append(")");

        //Execute the remove
        try (PreparedStatement statement = con.prepareStatement(sqlString.toString())) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Update Changes per day
        for (LootBlockValue value : values)
            StructureReloot.getInstance().getRelootActivityLogger().logRemoveBlock(value);
        close(con);
    }
}
