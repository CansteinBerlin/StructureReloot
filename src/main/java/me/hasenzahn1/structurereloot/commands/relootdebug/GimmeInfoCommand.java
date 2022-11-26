package me.hasenzahn1.structurereloot.commands.relootdebug;

import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


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
