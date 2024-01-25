package me.hasenzahn1.structurereloot.commands.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.config.LanguageConfig;
import me.hasenzahn1.structurereloot.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RelootResetCommand extends SubCommand {

    public RelootResetCommand(BaseCommand parent) {
        super(parent, "reset", "structurereloot.command.reset");
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.invalidCommand",
                    "command", getCommandHistory(),
                    "args", StringUtils.listToCommandArgs(tabComplete(null, new String[]{""})) +
                            " " +
                            StringUtils.listToCommandArgs(tabComplete(null, new String[]{"", ""}))));
            return true;
        }
        World world = Bukkit.getWorld(args[1]);
        if (world == null) {
            sender.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.reset.invalidWorld", "world", args[1]));
            return true;
        }

        if (args[0].equalsIgnoreCase("entity")) {
            StructureReloot.getInstance().getDatabaseManager().getDatabase(world).removeAllEntitys();
            sender.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.reset.removedEntities"));
        } else if (args[0].equalsIgnoreCase("block")) {
            StructureReloot.getInstance().getDatabaseManager().getDatabase(world).removeAllBlocks();
            sender.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.reset.removedBlocks"));
        } else {
            sender.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.invalidCommand",
                    "command", getCommandHistory(),
                    "args", StringUtils.listToCommandArgs(tabComplete(null, new String[]{""})) +
                            " " +
                            StringUtils.listToCommandArgs(tabComplete(null, new String[]{"", ""}))));
        }

        return true;
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1)
            return Arrays.stream(new String[]{"entity", "block"}).filter(s -> s.startsWith(args[0])).sorted().collect(Collectors.toList());
        if (args.length == 2)
            return Bukkit.getWorlds().stream()
                    .map(World::getName)
                    .filter(s -> s.startsWith(args[1]))
                    .sorted()
                    .collect(Collectors.toList());
        return new ArrayList<>();
    }
}
