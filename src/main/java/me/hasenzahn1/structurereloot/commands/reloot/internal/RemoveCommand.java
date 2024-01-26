package me.hasenzahn1.structurereloot.commands.reloot.internal;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commands.reloot.RelootListLootablesCommand;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
import me.hasenzahn1.structurereloot.database.LootValue;
import me.hasenzahn1.structurereloot.database.WorldDatabase;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RemoveCommand extends SubCommand {

    public RemoveCommand(BaseCommand parent) {
        super(parent, "remove", "structurereloot.command.reset");
    }

    @Override
    public void sendInvalidCommandMessage(CommandSender sender) {
        
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        if (args.length != 3) {
            return true;
        }

        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            return true;
        }

        if (!args[1].equalsIgnoreCase("block") && !args[1].equalsIgnoreCase("entity")) {
            return true;
        }

        WorldDatabase database = StructureReloot.getInstance().getDatabaseManager().getDatabase(world);
        int page;
        if (args[1].equalsIgnoreCase("block")) {
            LootBlockValue value = database.getBlock(LootValue.getLocFromString(world, args[2]));
            if (value == null) return true;
            List<LootBlockValue> values = database.getAllBlocks();
            page = values.indexOf(value) / 10;
            database.removeBlock(value);

        } else {
            LootEntityValue value = database.getEntity(LootValue.getLocFromString(world, args[2]));
            if (value == null) return true;
            List<LootEntityValue> values = database.getAllEntities();
            page = values.indexOf(value) / 10;
            database.removeEntity(value);
        }

        RelootListLootablesCommand.listAllElements(sender,
                world,
                page,
                args[1].equalsIgnoreCase("block") ? database.getAllBlocks() : database.getAllEntities(),
                args[1].equalsIgnoreCase("block") ? "blocks" : "entities");

        return true;
    }
}
