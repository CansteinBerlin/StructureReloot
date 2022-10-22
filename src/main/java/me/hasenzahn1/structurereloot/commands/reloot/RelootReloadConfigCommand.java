package me.hasenzahn1.structurereloot.commands.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.config.update.BlockUpdateConfig;
import me.hasenzahn1.structurereloot.config.update.EntityUpdateConfig;
import me.hasenzahn1.structurereloot.util.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RelootReloadConfigCommand extends SubCommand {

    public RelootReloadConfigCommand(BaseCommand parent) {
        super(parent, "reloadConfig", "structurereloot.command.reloadConfig");
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        if (args.length == 0 || args.length > 2) {
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.invalidCommand",
                    "command", getCommandHistory(),
                    "args", StringUtils.listToCommandArgs(tabComplete(sender, new String[]{""}))));

            return true;
        }

        boolean replace = false;
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("-o")) {
                replace = true;
            }
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "lang" -> {
                if (replace) {
                    StructureReloot.getInstance().getLanguageConfig().delete();
                    StructureReloot.getInstance().setLanguageConfig(null);
                }
                StructureReloot.getInstance().reloadLanguageConfig();
            }

            case "config" -> {
                if (replace) {
                    StructureReloot.getInstance().getDefaultConfig().delete();
                    StructureReloot.getInstance().setDefaultConfig(null);
                }
                StructureReloot.getInstance().initDefaultConfig();
            }

            case "entityupdatesettings" -> {
                if(replace){
                    StructureReloot.getInstance().getEntityUpdateConfig().delete();
                    StructureReloot.getInstance().setEntityUpdateConfig(null);
                }
                StructureReloot.getInstance().setEntityUpdateConfig(new EntityUpdateConfig());
            }

            case "blockupdatesettings" -> {
                if(replace){
                    StructureReloot.getInstance().getBlockUpdateConfig().delete();
                    StructureReloot.getInstance().setBlockUpdateConfig(null);
                }
                StructureReloot.getInstance().setBlockUpdateConfig(new BlockUpdateConfig());
            }

            default -> {
                sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.invalidCommand",
                        "command", getCommandHistory(),
                        "args", StringUtils.listToCommandArgs(tabComplete(sender, new String[]{""}))));
                return true;
            }
        }


        if (!replace)
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.reloadConfig.reloadSuccess",
                    "config", args[0].toLowerCase(Locale.ROOT)));
        else sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.reloadConfig.replaceSuccess",
                "config", args[0].toLowerCase(Locale.ROOT)));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1)
            return Arrays.stream(new String[]{"lang", "config", "entityUpdateSettings", "blockUpdateSettings"})
                    .filter(s -> s.startsWith(args[0]))
                    .sorted()
                    .collect(Collectors.toList());

        if (args.length == 2) return Arrays.stream(new String[]{"-o"})
                .filter(s -> s.startsWith(args[1]))
                .sorted()
                .collect(Collectors.toList());

        return new ArrayList<>();
    }
}
