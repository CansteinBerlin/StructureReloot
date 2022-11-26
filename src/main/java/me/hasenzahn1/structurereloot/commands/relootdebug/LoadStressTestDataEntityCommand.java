package me.hasenzahn1.structurereloot.commands.relootdebug;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoadStressTestDataEntityCommand extends SubCommand {


    public LoadStressTestDataEntityCommand(BaseCommand parent) {
        super(parent, "loadStressTestDataEntity", null);
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(StructureReloot.PREFIX + "§cNo amount provided");
            return true;
        }

        World world = Bukkit.getWorld("world_the_end");
        int amount = 0;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(StructureReloot.PREFIX + "§cAmount has to be int");
            return true;
        }

        int div = (int) (Math.sqrt(amount));
        int x = 0;
        int y = 0;
        List<LootEntityValue> values = new ArrayList<>();
        for (int i = 1; i < amount + 1; i++) {
            values.add(new LootEntityValue(
                    EntityType.ITEM_FRAME, new Location(world, 3000 + x * 16, 204, y * 16), null, UUID.randomUUID()
            ));
            x = i % div;
            y = i / div;
        }
        StructureReloot.getInstance().getDatabase(world).addMultipleEntities(values);

        sender.sendMessage(StructureReloot.PREFIX + "§aAdded " + amount + " new LootEntities");
        return true;
    }
}
