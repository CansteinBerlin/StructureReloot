package me.hasenzahn1.structurereloot.commands;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.util.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class RelootDebugCommand extends BaseCommand {

    public RelootDebugCommand() {
        super("relootDebug", "structurereloot.commandrelootDebug");

        for (Class<? extends SubCommand> command : ReflectionUtil.getAllClasses("me.hasenzahn1.structurereloot.commands.relootdebug", SubCommand.class)) {
            try {
                addSubCommand(command.getConstructor(BaseCommand.class).newInstance(this));
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                StructureReloot.getInstance().getRelootActivityLogger().log(Level.SEVERE, "Could not register Subcommand for " + name);
            }
        }
    }
}
