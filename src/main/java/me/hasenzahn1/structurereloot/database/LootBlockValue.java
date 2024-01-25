package me.hasenzahn1.structurereloot.database;


import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

@Getter
public class LootBlockValue extends LootValue {

    private final Material blockMaterial;
    private final BlockFace facing;

    public LootBlockValue(Location loc, LootTable lootTable) {
        super(loc, lootTable);

        blockMaterial = loc.getBlock().getType();
        BlockData data = loc.getBlock().getBlockData();
        if (data instanceof Directional) {
            facing = ((Directional) data).getFacing();
        } else {
            facing = BlockFace.NORTH;
        }
    }

    public LootBlockValue(Location loc, LootTable lootTable, Material blockMaterial, BlockFace facing) {
        super(loc, lootTable);

        this.blockMaterial = blockMaterial;
        this.facing = facing;
    }

    public LootBlockValue(World world, String loc, NamespacedKey lootTable, String blockMaterial, String facing) {
        super(world, loc, lootTable);

        this.blockMaterial = Material.valueOf(blockMaterial);
        this.facing = BlockFace.valueOf(facing);
    }

    @Override
    public void reloot() {
        location.getBlock().setType(Material.AIR); //Reset block
        location.getBlock().setType(blockMaterial); //Set Block

        //Set Directional Block Data
        if (location.getBlock().getBlockData() instanceof Directional data) {
            data.setFacing(facing); //Set Facing Direction
            location.getBlock().setBlockData(data);
        }

        //Set Loottable of Lootable Chest and Dispenser
        BlockState state = location.getBlock().getState();
        if (state instanceof Lootable) {
            ((Lootable) state).setLootTable(lootTable);
            state.update();
        }
    }


    //Getter and Setter
    public String getLocationString() {
        return locationToLocationString(location);
    }

    public String getBlockMaterialString() {
        return blockMaterial.name();
    }

    public String getFacingString() {
        return facing.name();
    }


    @Override
    public String toString() {
        return "LootBlockValue{" +
                "loc=" + location +
                ", lootTable=" + lootTable.toString() +
                ", blockMaterial=" + blockMaterial +
                ", facing=" + facing +
                '}';
    }
}
