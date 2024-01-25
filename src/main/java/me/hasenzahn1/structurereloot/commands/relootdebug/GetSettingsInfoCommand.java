package me.hasenzahn1.structurereloot.commands.relootdebug;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.config.LanguageConfig;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.general.RelootSettings;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class GetSettingsInfoCommand extends SubCommand {


    public GetSettingsInfoCommand(BaseCommand parent) {
        super(parent, "settingsInfo", null);
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
        sender.sendMessage("§aDuration: §6" + setting.durationPattern + " §ain seconds: §6" + setting.duration);
        sender.sendMessage("§aNextDate: §6" + setting.nextDate + " §ain §6" + ChronoUnit.SECONDS.between(LocalDateTime.now(), setting.nextDate));
        sender.sendMessage("§aReloot On Startup: §6" + setting.isRelootOnStartup());
        sender.sendMessage("§aMax Reloot Amount: §6" + setting.getMaxRelootAmount());


        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Bukkit.getWorlds().stream().map(World::getName).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }
}
