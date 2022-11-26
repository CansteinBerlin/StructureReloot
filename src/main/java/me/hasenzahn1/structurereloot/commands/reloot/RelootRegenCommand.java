package me.hasenzahn1.structurereloot.commands.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.reloot.RelootHelper;
import me.hasenzahn1.structurereloot.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RelootRegenCommand extends SubCommand {

    public RelootRegenCommand(BaseCommand parent) {
        super(parent, "regen", "structurereloot.command.regen");
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.invalidCommand",
                    "command", getCommandHistory(),
                    "args", StringUtils.listToCommandArgs(tabComplete(null, new String[]{""})) +
                            " " +
                            StringUtils.listToCommandArgs(tabComplete(null, new String[]{"", ""})) +
                            " " +
                            "<1/2/.../all>"
            ));

            return true;
        }

        //Check World
        World world = Bukkit.getWorld(args[1]);
        if (world == null) {
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.reset.invalidWorld", "world", args[1]));
            return true;
        }

        //Check Amount
        int amount;
        if (args[2].equalsIgnoreCase("all")) {
            amount = Integer.MAX_VALUE;
        } else {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.invalidCommand",
                        "command", getCommandHistory(),
                        "args", StringUtils.listToCommandArgs(tabComplete(null, new String[]{""})) +
                                " " +
                                StringUtils.listToCommandArgs(tabComplete(null, new String[]{"", ""})) +
                                " " +
                                "<1/2/.../all>"
                ));
                return true;
            }
        }

        long millis = System.currentTimeMillis();

        if (args[0].equalsIgnoreCase("entity")) {
            RelootHelper.regenNEntities(world, amount, () -> {
                sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.regen.sucEntity",
                        "amount", args[2].toLowerCase(Locale.ROOT),
                        "time", ((System.currentTimeMillis() - millis) / 1000) + ""));
            });

        } else if (args[0].equalsIgnoreCase("block")) {
            RelootHelper.regenNBlocks(world, amount, () ->
                    sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.regen.sucBlock",
                            "amount", args[2].toLowerCase(Locale.ROOT),
                            "time", ((System.currentTimeMillis() - millis) / 1000) + ""))
            );

        } else if (args[0].equalsIgnoreCase("all")) {
            RelootHelper.regenNEntities(world, amount, () -> {
            });
            RelootHelper.regenNBlocks(world, amount, () -> {
                        sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.regen.sucBoth",
                                "amount", args[2].toLowerCase(Locale.ROOT),
                                "time", ((System.currentTimeMillis() - millis) / 1000) + ""));
                    }
            );


        } else {
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.invalidCommand",
                    "command", getCommandHistory(),
                    "args", StringUtils.listToCommandArgs(tabComplete(null, new String[]{""})) +
                            " " +
                            StringUtils.listToCommandArgs(tabComplete(null, new String[]{"", ""})) +
                            " " +
                            "<1/2/.../all>"
            ));
        }
        return true;
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(new String[]{"entity", "block", "all"})
                    .filter(s -> s.startsWith(args[0]))
                    .sorted()
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            return Bukkit.getWorlds().stream()
                    .map(World::getName)
                    .filter(s -> s.startsWith(args[1]))
                    .sorted()
                    .collect(Collectors.toList());
        }
        if (args.length == 3) {
            return Arrays.stream(new String[]{"1", "2", "3", "...", "all"})
                    .filter(s -> s.startsWith(args[2]))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
