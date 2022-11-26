package me.hasenzahn1.structurereloot.database.tables;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
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

    private boolean cacheRemove;
    private final ArrayList<LootBlockValue> cachedLootBlockValues;

    public BlockTable(Database database, World world) {
        super("blocks", database);
        this.world = world;

        cacheRemove = false;
        cachedLootBlockValues = new ArrayList<>();
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
        try (PreparedStatement statement = con.prepareStatement(
                "SELECT * FROM " + getTableName() + " WHERE location='" + LootBlockValue.locationToLocationString(loc) + "'"
        )) {
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                value = new LootBlockValue(world, set.getString("location"), getNamespacedKey(set.getString("lootTable")), set.getString("block"), set.getString("facing"));
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
        try (PreparedStatement statement = con.prepareStatement(
                "SELECT * FROM " + getTableName()
        )) {
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                values.add(new LootBlockValue(world, set.getString("location"), getNamespacedKey(set.getString("lootTable")), set.getString("block"), set.getString("facing")));
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
        try (PreparedStatement statement = con.prepareStatement(
                "INSERT OR REPLACE INTO " + getTableName() + " (location, lootTable, block, facing) VALUES(?,?,?,?)"
        )) {
            statement.setString(1, value.getLocationString());
            statement.setString(2, value.getStringLootTable());
            statement.setString(3, value.getBlockMaterialString());
            statement.setString(4, value.getFacingString());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Update Changes per day
        StructureReloot.getInstance().getChangesPerDay().markAddBlock(value);
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
        for (LootBlockValue value : values) StructureReloot.getInstance().getChangesPerDay().markAddBlock(value);
    }

    public void _addMultipleBlocks(List<LootBlockValue> values) {
        Connection con = getConnection();
        if (values.size() == 0) return;
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
        if (cacheRemove) {
            cachedLootBlockValues.add(value);
            return;
        }
        Connection con = getConnection();
        try (PreparedStatement statement = con.prepareStatement(
                "DELETE FROM " + getTableName() + " WHERE location='" + value.getLocationString() + "'"
        )) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Update Changes per day
        StructureReloot.getInstance().getChangesPerDay().markRemoveBlock(value);
        close(con);
    }

    public void removeMultipleBlocks(List<LootBlockValue> values) {
        if (values.size() == 0) return;
        Connection con = getConnection();
        StringBuilder sqlString = new StringBuilder("DELETE FROM ").append(getTableName()).append(" WHERE location IN ('").append(values.get(0).getLocationString()).append("'");

        for (int i = 1; i < values.size(); i++) {
            sqlString.append(", '").append(values.get(i).getLocationString()).append("'");
        }
        sqlString.append(")");
        try (PreparedStatement statement = con.prepareStatement(sqlString.toString())) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Update Changes per day
        for (LootBlockValue value : values) StructureReloot.getInstance().getChangesPerDay().markRemoveBlock(value);
        close(con);
    }

    //Caching
    public void setCacheRemove(boolean value) {
        this.cacheRemove = value;
        if (!value) {
            removeMultipleBlocks(cachedLootBlockValues);
            cachedLootBlockValues.clear();
        }
    }


    //Whatever
    public static NamespacedKey getNamespacedKey(String lootTable) {
        String[] strings = lootTable.split(":");
        if (strings.length == 1) return null;
        return new NamespacedKey(strings[0], strings[1]);
    }

}
