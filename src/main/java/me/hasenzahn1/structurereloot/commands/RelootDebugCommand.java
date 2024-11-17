package me.hasenzahn1.structurereloot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.hasenzahn1.structurereloot.listeners.EntityListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

@CommandAlias("relootdebug")
@CommandPermission("structurereloot.command.relootdebug")
public class RelootDebugCommand extends BaseCommand {

    @Subcommand("summonItemFrame")
    public void summonItemFrame(Player player) {
        Location loc = player.getLocation();
        ItemFrame frame = loc.getWorld().spawn(loc, ItemFrame.class);
        frame.getPersistentDataContainer().set(EntityListener.markEntityKey, PersistentDataType.BYTE, (byte) 1);
        frame.setItem(new ItemStack(Material.ELYTRA));
    }

}
