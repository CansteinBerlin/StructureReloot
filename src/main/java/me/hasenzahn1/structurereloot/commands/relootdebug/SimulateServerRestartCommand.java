package me.hasenzahn1.structurereloot.commands.relootdebug;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import org.bukkit.command.CommandSender;

public class SimulateServerRestartCommand extends SubCommand {

    public SimulateServerRestartCommand(BaseCommand parent) {
        super(parent, "simulateServerRestart", null);
    }
    
    @Override
    public boolean performCommand(CommandSender sender, String[] args) {

        StructureReloot.getInstance().relootElementsInWorld(true);
        sender.sendMessage("Simulated a server restart");

        return true;
    }
}
