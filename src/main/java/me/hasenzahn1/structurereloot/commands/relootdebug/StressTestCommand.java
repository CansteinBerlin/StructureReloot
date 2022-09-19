package me.hasenzahn1.structurereloot.commands.relootdebug;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.loot.LootTables;

public class StressTestCommand extends SubCommand {

    public StressTestCommand(BaseCommand parent) {
        super(parent, "loadStresstestData", null);
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        if(args.length != 1){
            sender.sendMessage(StructureReloot.PREFIX + "§cNo amount provided");
            return true;
        }

        World world = Bukkit.getWorld("world_the_end");
        int amount = 0;
        try {
            amount = Integer.parseInt(args[0]);
        }catch (IllegalArgumentException e){
            sender.sendMessage(StructureReloot.PREFIX + "§cAmount has to be int");
            return true;
        }

        int div = (int) (Math.sqrt(amount));
        int x = 0;
        int y = 0;

        for(int i = 1; i < amount + 1; i++){
            StructureReloot.getInstance().getDatabase(world).addBlock(new LootBlockValue(
                    new Location(world, 3000 + x * 16, 200, y * 16),
                    LootTables.ABANDONED_MINESHAFT.getLootTable(),
                    Material.CHEST,
                    BlockFace.NORTH
            ));
            x = i % div;
            y = i / div;
        }

        return true;
    }


}
