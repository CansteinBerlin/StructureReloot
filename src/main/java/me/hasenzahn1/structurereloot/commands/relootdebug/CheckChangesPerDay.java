package me.hasenzahn1.structurereloot.commands.relootdebug;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import org.bukkit.command.CommandSender;

public class CheckChangesPerDay extends SubCommand {

    public CheckChangesPerDay(BaseCommand parent) {
        super(parent, "checkChangesPerDay", null);
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        sender.sendMessage(StructureReloot.getInstance().getChangesPerDay().toString());
        return true;
    }
}
