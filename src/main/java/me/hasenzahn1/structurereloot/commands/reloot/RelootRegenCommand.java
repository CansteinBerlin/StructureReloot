package me.hasenzahn1.structurereloot.commands.reloot;

import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import org.bukkit.command.CommandSender;

public class RelootRegenCommand extends SubCommand {

    public RelootRegenCommand(BaseCommand parent) {
        super(parent, "regen", "reloot.commands.regen");
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        return true;
    }
}
