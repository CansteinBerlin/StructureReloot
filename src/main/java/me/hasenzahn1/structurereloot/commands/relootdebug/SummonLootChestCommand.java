package me.hasenzahn1.structurereloot.commands.relootdebug;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTables;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SummonLootChestCommand extends SubCommand {

    public SummonLootChestCommand(BaseCommand parent) {
        super(parent, "summonLootchest", null);
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(StructureReloot.PREFIX + "§cYou have to be a player to use this command");
            return true;
        }
        LootTables lootTable = LootTables.ABANDONED_MINESHAFT;
        if (args.length >= 1) {
            try {
                lootTable = LootTables.valueOf(args[0]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(StructureReloot.PREFIX + "§cUnknown LootTable " + args[0]);
                return true;
            }
        }

        Player p = ((Player) sender);
        p.getLocation().getBlock().setType(Material.AIR);
        p.getLocation().getBlock().setType(Material.CHEST);
        BlockState state = p.getLocation().getBlock().getState();
        if (!(state instanceof Chest)) {
            sender.sendMessage(StructureReloot.PREFIX + "§cError placing chest");
            return true;
        }
        Chest chestState = ((Chest) state);
        chestState.setLootTable(lootTable.getLootTable());
        chestState.update();

        sender.sendMessage(StructureReloot.PREFIX + "§aSuccessfully placed chest");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Arrays.stream(LootTables.values())
                .filter(l -> l.getKey().getKey().contains("chest"))
                .map(Enum::name)
                .filter(s -> s.startsWith(args[0]))
                .sorted()
                .collect(Collectors.toList());
    }
}
