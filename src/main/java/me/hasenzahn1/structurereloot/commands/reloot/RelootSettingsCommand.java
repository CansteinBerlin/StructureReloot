package me.hasenzahn1.structurereloot.commands.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.util.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;

public class RelootSettingsCommand extends SubCommand {
    public RelootSettingsCommand(BaseCommand parent) {
        super(parent, "settings", "reloot.commands.settings");

        for (Class<? extends SubCommand> command : ReflectionUtil.getAllClasses("me.hasenzahn1.structurereloot.commands.reloot.settings", SubCommand.class)) {
            try {
                addSubCommand(command.getConstructor(BaseCommand.class).newInstance(this));
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                    InvocationTargetException e) {
                StructureReloot.LOGGER.severe("§cCould not register Subcommand for " + name);
            }
        }
    }
}
