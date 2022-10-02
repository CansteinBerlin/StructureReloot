package me.hasenzahn1.structurereloot.commands.reloot.settings;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.general.RelootSettings;
import me.hasenzahn1.structurereloot.commands.reloot.RelootInfoCommand;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SetRelootOnStartup extends SubCommand {

    public SetRelootOnStartup(BaseCommand parent) {
        super(parent, "setRelootOnStartup", null);
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        if(args.length != 3){
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.invalidCommand",
                    "command", getCommandHistory(),
                    "args", "<world> <block/entity> <true/false>"));
            return true;
        }

        World world = Bukkit.getWorld(args[1]);
        if(world == null){
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.reset.invalidWorld", "world", args[1]));
            return true;
        }
        if (!args[0].equalsIgnoreCase("block") && !args[0].equalsIgnoreCase("entity")) {
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.settings.invalidType", "value", args[0]));
            return true;
        }
        if (!args[2].equalsIgnoreCase("true") && !args[2].equalsIgnoreCase("false")) {
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.settings.invalidBool", "value", args[2]));
            return true;
        }

        RelootSettings settings = args[0].equalsIgnoreCase("block") ? StructureReloot.getInstance().getBlockUpdateConfig().getSettingsForWorld(world) : StructureReloot.getInstance().getEntityUpdateConfig().getSettingsForWorld(world);

        settings.setRelootOnStartup(Boolean.parseBoolean(args[2]));

        if(args[0].equalsIgnoreCase("block")){
            StructureReloot.getInstance().getBlockUpdateConfig().update();
        }else{
            StructureReloot.getInstance().getEntityUpdateConfig().update();
        }

        RelootInfoCommand.sendPlayerInfoScreen(sender, world);

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Arrays.stream(new String[]{"true", "false"}).filter(s -> s.startsWith(args[0])).sorted().collect(Collectors.toList());
    }
}
