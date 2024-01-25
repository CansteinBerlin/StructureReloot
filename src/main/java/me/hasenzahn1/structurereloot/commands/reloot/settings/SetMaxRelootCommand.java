package me.hasenzahn1.structurereloot.commands.reloot.settings;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commands.reloot.RelootInfoCommand;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.config.LanguageConfig;
import me.hasenzahn1.structurereloot.general.RelootSettings;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class SetMaxRelootCommand extends SubCommand {

    public SetMaxRelootCommand(BaseCommand parent) {
        super(parent, "setMaxRelootAmount", null);
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.invalidCommand",
                    "command", getCommandHistory(),
                    "args", "<world> <block/entity> <amount>"));
            return true;
        }

        World world = Bukkit.getWorld(args[1]);
        if (world == null) {
            sender.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.reset.invalidWorld", "world", args[1]));
            return true;
        }
        if (!args[0].equalsIgnoreCase("block") && !args[0].equalsIgnoreCase("entity")) {
            sender.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.settings.invalidType", "value", args[0]));
            return true;
        }
        if (!isInt(args[2])) {
            sender.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.settings.invalidInt", "value", args[2]));
            return true;
        }

        RelootSettings settings = args[0].equalsIgnoreCase("block") ? StructureReloot.getInstance().getBlockUpdateConfig().getSettingsForWorld(world) : StructureReloot.getInstance().getEntityUpdateConfig().getSettingsForWorld(world);

        settings.setMaxRelootAmount(Integer.parseInt(args[2]));

        if (args[0].equalsIgnoreCase("block")) {
            StructureReloot.getInstance().getBlockUpdateConfig().update();
        } else {
            StructureReloot.getInstance().getEntityUpdateConfig().update();
        }

        RelootInfoCommand.sendPlayerInfoScreen(sender, world);

        return true;
    }

    public static boolean isInt(String s) {
        try {

            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
