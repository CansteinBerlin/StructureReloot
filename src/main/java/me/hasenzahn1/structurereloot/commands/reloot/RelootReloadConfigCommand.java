package me.hasenzahn1.structurereloot.commands.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.config.LanguageConfig;
import me.hasenzahn1.structurereloot.config.UpdateConfig;
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
    public void sendInvalidCommandMessage(CommandSender sender) {
        sender.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.invalidCommand",
                "command", getCommandHistory(),
                "args", StringUtils.listToCommandArgs(tabComplete(sender, new String[]{""})))
        );

    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        if (args.length == 0 || args.length > 2) {
            sendInvalidCommandMessage(sender);
            return true;
        }

        boolean replace = false;
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("-o")) {
                replace = true;
            }
        }

        StructureReloot instance = StructureReloot.getInstance();

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "lang" -> {
                if (replace) {
                    instance.getLanguageConfig().delete();
                }
                instance.getLanguageConfig().reloadConfig();
            }

            case "config" -> {
                if (replace) {
                    instance.getDefaultConfig().delete();
                    instance.setDefaultConfig(null);
                }
                instance.initDefaultConfig();
            }

            case "entityupdatesettings" -> {
                if (replace) {
                    instance.getEntityUpdateConfig().delete();
                }
                instance.setEntityUpdateConfig(new UpdateConfig(instance.getEntityUpdateConfig().getName()));
            }

            case "blockupdatesettings" -> {
                if (replace) {
                    instance.getBlockUpdateConfig().delete();
                }
                instance.setBlockUpdateConfig(new UpdateConfig(instance.getBlockUpdateConfig().getName()));
            }

            default -> {
                sendInvalidCommandMessage(sender);
                return true;
            }
        }


        if (!replace)
            sender.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.reloadConfig.reloadSuccess",
                    "config", args[0].toLowerCase(Locale.ROOT)));
        else sender.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.reloadConfig.replaceSuccess",
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
