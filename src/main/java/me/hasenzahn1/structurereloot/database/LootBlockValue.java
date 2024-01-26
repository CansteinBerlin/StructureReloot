package me.hasenzahn1.structurereloot.database;


import lombok.Getter;
import me.hasenzahn1.structurereloot.listeners.BlockListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrushableBlock;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.bukkit.persistence.PersistentDataType;

@Getter
public class LootBlockValue extends LootValue {

    private final Material blockMaterial;
    private final BlockFace facing;

    /**
     * Create a new LootBlockValue through code.
     *
     * @param loc       The location the block is at
     * @param lootTable The lootTable the block has
     */
    public LootBlockValue(Location loc, LootTable lootTable) {
        super(loc, lootTable);

        blockMaterial = loc.getBlock().getType();
        BlockData data = loc.getBlock().getBlockData();
        //Some blocks are not directional, The Suspicious sand block as an example. Defaults to NORTH
        if (data instanceof Directional) {
            facing = ((Directional) data).getFacing();
        } else {
            facing = BlockFace.NORTH;
        }
    }

    /**
     * Create a new LootBlockValue through code
     *
     * @param loc           The location the block is at
     * @param lootTable     The lootTable the block has
     * @param blockMaterial The material the block has
     * @param facing        The Direction the block is facing
     */
    public LootBlockValue(Location loc, LootTable lootTable, Material blockMaterial, BlockFace facing) {
        super(loc, lootTable);

        this.blockMaterial = blockMaterial;
        this.facing = facing;
    }

    /**
     * Create a new LootBlockValue form the database
     *
     * @param world         The World the block is in
     * @param loc           The Location the block is at
     * @param lootTable     The lootTable the block has
     * @param blockMaterial The material the block has
     * @param facing        The direction the block is facing
     */
    public LootBlockValue(World world, String loc, NamespacedKey lootTable, String blockMaterial, String facing) {
        super(world, loc, lootTable);

        this.blockMaterial = Material.valueOf(blockMaterial);
        this.facing = BlockFace.valueOf(facing);
    }

    /**
     * Method to reloot that specific block
     */
    @Override
    public void reloot() {
        location.getBlock().setType(Material.AIR); //Reset block
        location.getBlock().setType(blockMaterial); //Set Block

        //Set Directional Block Data
        if (location.getBlock().getBlockData() instanceof Directional data) {
            data.setFacing(facing); //Set Facing Direction
            location.getBlock().setBlockData(data);
        }

        //Set Loottable of Lootable Blocks
        BlockState state = location.getBlock().getState();
        if (state instanceof Lootable) {
            ((Lootable) state).setLootTable(lootTable);

            //Suspicious Blocks have to be marked as they lose their LootTable
            if (state instanceof BrushableBlock) {
                ((BrushableBlock) state).getPersistentDataContainer().set(BlockListener.SAVED_LOOT_TABLE, PersistentDataType.STRING, lootTable + "");
            }
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
