package me.hasenzahn1.structurereloot.commands.relootdebug;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.general.RelootSettings;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class GetBlockUpdateInfo extends SubCommand {
    public GetBlockUpdateInfo(BaseCommand parent) {
        super(parent, "blockUpdateInfo", null);
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(StructureReloot.PREFIX + "§cNo Player");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(StructureReloot.PREFIX + "§cUse " + getCommandHistory() + " <world>");
            return true;
        }

        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            sender.sendMessage(StructureReloot.PREFIX + "§cUnknown world: " + args[0]);
            return true;
        }


        RelootSettings setting = StructureReloot.getInstance().getBlockUpdateConfig().getSettingsForWorld(world);
        sender.sendMessage(setting.toString());

        sender.sendMessage(StructureReloot.getInstance().getBlockUpdateConfig().getNeededUpdates() + "");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Bukkit.getWorlds().stream().map(World::getName).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }
}


