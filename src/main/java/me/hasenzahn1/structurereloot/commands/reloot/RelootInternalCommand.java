package me.hasenzahn1.structurereloot.commands.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.util.ReflectionUtil;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class RelootInternalCommand extends SubCommand {

    public RelootInternalCommand(BaseCommand parent) {
        super(parent, "internal", null);

        for (Class<? extends SubCommand> command : ReflectionUtil.getAllClasses("me.hasenzahn1.structurereloot.commands.reloot.internal", SubCommand.class)) {
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
