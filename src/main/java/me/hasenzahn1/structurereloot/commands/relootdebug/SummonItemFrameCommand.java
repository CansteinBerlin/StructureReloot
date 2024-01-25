package me.hasenzahn1.structurereloot.commands.relootdebug;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.listeners.EntityListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class SummonItemFrameCommand extends SubCommand {

    public SummonItemFrameCommand(BaseCommand parent) {
        super(parent, "summonItemFrame", null);
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(StructureReloot.PREFIX + "§cYou have to be a player to use this command");
            return true;
        }
        Player p = ((Player) sender);
        Location loc = p.getLocation();
        ItemFrame frame = loc.getWorld().spawn(loc, ItemFrame.class);
        frame.getPersistentDataContainer().set(EntityListener.markEntityKey, PersistentDataType.BYTE, (byte) 1);
        frame.setItem(new ItemStack(Material.ELYTRA));
        return true;
    }
}
