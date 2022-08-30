package me.hasenzahn1.structurereloot.commands.relootdebug;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

public class SummonItemFrameCommand extends SubCommand {

    public SummonItemFrameCommand(BaseCommand parent) {
        super(parent, "summonItemFrame", null);
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(StructureReloot.PREFIX + "§cYou have to be a player to use this command");
            return true;
        }
        Player p = ((Player) sender);
        Location loc = p.getLocation();
        loc.getWorld().spawn(loc, ItemFrame.class);

        return true;
    }
}
