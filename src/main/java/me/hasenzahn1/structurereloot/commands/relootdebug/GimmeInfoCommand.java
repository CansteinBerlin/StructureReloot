package me.hasenzahn1.structurereloot.commands.relootdebug;

import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.type.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.loot.Lootable;


public class GimmeInfoCommand extends SubCommand {

    public GimmeInfoCommand(BaseCommand parent) {
        super(parent, "gimmeInfo", null);
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        sender.sendMessage(player.getWorld().getEnderDragonBattle() + "");
        return true;
    }
}
