package me.hasenzahn1.structurereloot.autoupdate;

import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.database.LootEntityValue;

import java.util.ArrayList;

public class ChangesPerDay {

    private final ArrayList<LootBlockValue> blockAdditions;
    private final ArrayList<LootEntityValue> entityAdditions;
    private final ArrayList<LootBlockValue> blockRemovals;
    private final ArrayList<LootEntityValue> entityRemovals;

    public ChangesPerDay(){
        blockAdditions = new ArrayList<>();
        blockRemovals = new ArrayList<>();
        entityAdditions = new ArrayList<>();
        entityRemovals = new ArrayList<>();
    }

    public void markAddBlock(LootBlockValue value){
        blockAdditions.add(value);
    }

    public void markAddEntity(LootEntityValue value){
        entityAdditions.add(value);
    }

    public void markRemoveBlock(LootBlockValue value){
        blockRemovals.add(value);
    }

    public void markRemoveEntity(LootEntityValue value){
        entityRemovals.add(value);
    }

    public void clear(){
        blockAdditions.clear();
        blockRemovals.clear();
        entityAdditions.clear();
        entityRemovals.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Additions to database: \n");
        for(LootBlockValue value : blockAdditions) {
            sb.append("\t").append("LootBlock at ").append(value.getLocationString()).append("\n");
        }
        for(LootEntityValue value : entityAdditions) {
            sb.append("\t").append("LootEntity at ").append(value.getLocationString()).append("\n");
        }
        sb.append("\n");
        sb.append("Removals from database: \n");
        for(LootBlockValue value : blockRemovals){
            sb.append("\t").append("LootBlock at ").append(value.getLocationString()).append("\n");
        }
        for(LootEntityValue value : entityRemovals) {
            sb.append("\t").append("LootEntity at ").append(value.getLocationString()).append("\n");
        }
        return sb.toString();
    }


}
