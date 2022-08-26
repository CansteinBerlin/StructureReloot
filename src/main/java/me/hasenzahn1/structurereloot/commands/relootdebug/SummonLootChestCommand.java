package me.hasenzahn1.structurereloot.commands.relootdebug;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTables;

public class SummonLootChestCommand extends SubCommand {

    public SummonLootChestCommand(BaseCommand parent) {
        super(parent, "summonLootchest", null);
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {

        if(!(sender instanceof Player)){
            sender.sendMessage(StructureReloot.PREFIX + "§cYou have to be a player to use this command");
            return true;
        }
        Player p = ((Player) sender);
        p.getLocation().getBlock().setType(Material.AIR);
        p.getLocation().getBlock().setType(Material.CHEST);
        BlockState state = p.getLocation().getBlock().getState();
        if(!(state instanceof Chest)){
            sender.sendMessage(StructureReloot.PREFIX + "§cError placing chest");
            return true;
        }
        Chest chestState = ((Chest) state);
        chestState.setLootTable(LootTables.ANCIENT_CITY.getLootTable());
        chestState.update();

        sender.sendMessage(StructureReloot.PREFIX + "§aSuccessfully placed chest");
        return true;
    }
}
