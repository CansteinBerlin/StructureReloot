package me.hasenzahn1.structurereloot.commands;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.util.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class RelootCommand extends BaseCommand {

    public RelootCommand() {
        super("reloot", "structurereloot.command.reloot");

        for (Class<? extends SubCommand> command : ReflectionUtil.getAllClasses("me.hasenzahn1.structurereloot.commands.reloot", SubCommand.class)) {
            if (!command.getPackage().getName().equals("me.hasenzahn1.structurereloot.commands.reloot")) continue;
            try {
                addSubCommand(command.getConstructor(BaseCommand.class).newInstance(this));
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                StructureReloot.getInstance().getRelootActivityLogger().log(Level.SEVERE, "Could not register Subcommand for " + name);
            }
        }
    }
}
