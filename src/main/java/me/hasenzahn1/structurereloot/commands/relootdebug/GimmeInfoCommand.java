package me.hasenzahn1.structurereloot.commands.relootdebug;

import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.type.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.loot.Lootable;


public class GimmeInfoCommand extends SubCommand {

    public GimmeInfoCommand(BaseCommand parent) {
        super(parent, "gimmeInfo", null);
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        sender.sendMessage("Block: " + ((Lootable) new Location(Bukkit.getWorld("world"), 1747, 73, -286).getBlock().getState()).getLootTable());
        sender.sendMessage("Block: " + (new Location(Bukkit.getWorld("world"), 1747, 73, -286).getBlock() instanceof Lootable));
        return true;
    }
}
