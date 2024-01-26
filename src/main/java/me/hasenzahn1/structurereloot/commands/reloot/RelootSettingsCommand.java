package me.hasenzahn1.structurereloot.commands.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.util.ReflectionUtil;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class RelootSettingsCommand extends SubCommand {
    public RelootSettingsCommand(BaseCommand parent) {
        super(parent, "settings", "structurereloot.command.settings");

        for (Class<? extends SubCommand> command : ReflectionUtil.getAllClasses("me.hasenzahn1.structurereloot.commands.reloot.settings", SubCommand.class)) {
            try {
                addSubCommand(command.getConstructor(BaseCommand.class).newInstance(this));
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                StructureReloot.getInstance().getRelootActivityLogger().log(Level.SEVERE, "Could not register Subcommand for " + name);
            }
        }
    }

    @Override
    public void sendInvalidCommandMessage(CommandSender sender) {
    }
}
