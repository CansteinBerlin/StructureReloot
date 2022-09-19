package me.hasenzahn1.structurereloot.database.tables;

import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.databasesystem.Database;
import me.hasenzahn1.structurereloot.databasesystem.Table;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;

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

    @Override
    public String getCreationString() {
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                "location varchar(27) PRIMARY KEY," +
                "lootTable varchar(57) NOT NULL," +
                "block varchar(40) NOT NULL," +
                "facing varchar(6) NOT NULL)" +
                ";";
    }

    public void removeLootBlockValue(LootBlockValue value){
        if(cacheRemove){
            cachedLootBlockValues.add(value);
            return;
        }
        Connection con = getConnection();
        try(PreparedStatement statement = con.prepareStatement(
                "DELETE FROM " + getTableName() + " WHERE location='" + value.getLocationString() + "'"
        )){
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBlock(LootBlockValue value){
        Connection con = getConnection();
        try(PreparedStatement statement = con.prepareStatement(
                "INSERT OR REPLACE INTO " + getTableName() + " (location, lootTable, block, facing) VALUES(?,?,?,?)"
        )){
            statement.setString(1, value.getLocationString());
            statement.setString(2, value.getStringLootTable());
            statement.setString(3, value.getBlockMaterialString());
            statement.setString(4, value.getFacingString());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LootBlockValue getBlock(Location loc){
        Connection con = getConnection();
        try(PreparedStatement statement = con.prepareStatement(
                "SELECT * FROM " + getTableName() + " WHERE location='" + LootBlockValue.locationToLocationString(loc) + "'"
        )){
            ResultSet set = statement.executeQuery();
            if(set.next()){
                return new LootBlockValue(world, set.getString("location"), getNamespacedKey(set.getString("lootTable")), set.getString("block"), set.getString("facing"));
            }else{
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static NamespacedKey getNamespacedKey(String lootTable) {
        String[] strings = lootTable.split(":");
        if(strings.length == 1) return null;
        return new NamespacedKey(strings[0], strings[1]);
    }

    public ArrayList<LootBlockValue> getAllBlocks(){
        Connection con = getConnection();
        try(PreparedStatement statement = con.prepareStatement(
                "SELECT * FROM " + getTableName()
        )) {
            ResultSet set = statement.executeQuery();
            ArrayList<LootBlockValue> values = new ArrayList<>();
            while(set.next()){
                values.add(new LootBlockValue(world, set.getString("location"), getNamespacedKey(set.getString("lootTable")), set.getString("block"), set.getString("facing")));
            }
            return values;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void removeCachedLbvs(){
        if(cachedLootBlockValues.size() == 0) return;
        Connection con = getConnection();
        StringBuilder sqlString = new StringBuilder("DELETE FROM ").append(getTableName()).append(" WHERE location IN ('").append(cachedLootBlockValues.get(0).getLocationString()).append("'");

        for(int i = 1; i < cachedLootBlockValues.size(); i++){
            sqlString.append(", '").append(cachedLootBlockValues.get(i).getLocationString()).append("'");
        }
        sqlString.append(")");
        Bukkit.broadcastMessage(sqlString.toString());
        try(PreparedStatement statement = con.prepareStatement(
                sqlString.toString()
        )){
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCacheRemove(boolean value){
        this.cacheRemove = value;
        if(!value) {
            Bukkit.broadcastMessage(cachedLootBlockValues.stream().map(LootBlockValue::getLocationString).collect(Collectors.toList()) + "");
            removeCachedLbvs();
            cachedLootBlockValues.clear();
        }
    }

}
