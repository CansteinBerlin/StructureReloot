package me.hasenzahn1.structurereloot.database.tables;

import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
import me.hasenzahn1.structurereloot.databasesystem.Database;
import me.hasenzahn1.structurereloot.databasesystem.Table;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static me.hasenzahn1.structurereloot.database.tables.BlockTable.getNamespacedKey;

public class EntityTable extends Table {

    private final World world;

    private boolean cacheRemove;
    private final ArrayList<LootEntityValue> cachedLootEntityValues;

    public EntityTable(Database database, World world) {
        super("entities", database);
        this.world = world;
        cacheRemove = false;
        cachedLootEntityValues = new ArrayList<>();
    }

    @Override
    public String getCreationString() {
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                "location varchar(27) PRIMARY KEY," +
                "lootTable varchar(57)," +
                "uuid varchar(36)," +
                "entityType varchar(20) NOT NULL" +
                ");";
    }

    public void removeLootEntityValue(LootEntityValue value){
        if(cacheRemove){
            cachedLootEntityValues.add(value);
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

    public void addEntity(LootEntityValue value){
        Connection con = getConnection();
        try(PreparedStatement statement = con.prepareStatement(
                "INSERT OR REPLACE INTO " + getTableName() + " (location, lootTable, uuid, entityType) VALUES(?,?,?,?)"
        )){
            statement.setString(1, value.getLocationString());
            statement.setString(2, value.getLootTableString());
            statement.setString(3, value.getUUIDString());
            statement.setString(4, value.getEntityString());
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LootEntityValue getEntity(Location loc){
        Connection con = getConnection();
        try(PreparedStatement statement = con.prepareStatement(
                "SELECT * FROM " + getTableName() + " WHERE location='" + LootBlockValue.locationToLocationString(loc) + "'"
        )){
            ResultSet set = statement.executeQuery();
            if(set.next()){
                return new LootEntityValue(world,
                        set.getString("entityType"),
                        set.getString("location"),
                        getNamespacedKey(set.getString("lootTable")),
                        set.getString("uuid"));
            }else{
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<LootEntityValue> getAllEntities(){
        Connection con = getConnection();
        try(PreparedStatement statement = con.prepareStatement(
                "SELECT * FROM " + getTableName()
        )) {
            ResultSet set = statement.executeQuery();
            ArrayList<LootEntityValue> values = new ArrayList<>();
            while(set.next()){
                values.add(new LootEntityValue(world,
                        set.getString("entityType"),
                        set.getString("location"),
                        getNamespacedKey(set.getString("lootTable")),
                        set.getString("uuid")));
            }
            return values;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void removeCachedLevs(){
        if(cachedLootEntityValues.size() == 0) return;
        Connection con = getConnection();
        StringBuilder sqlString = new StringBuilder("DELETE FROM ").append(getTableName()).append(" WHERE location IN ('").append(cachedLootEntityValues.get(0).getLocationString()).append("'");

        for(int i = 1; i <  cachedLootEntityValues.size(); i++){
            sqlString.append(", '").append(cachedLootEntityValues.get(i).getLocationString()).append("'");
        }
        sqlString.append(")");

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
            removeCachedLevs();
            cachedLootEntityValues.clear();
        }
    }

}
