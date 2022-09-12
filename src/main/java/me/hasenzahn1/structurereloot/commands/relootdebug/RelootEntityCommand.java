package me.hasenzahn1.structurereloot.commands.relootdebug;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
import me.hasenzahn1.structurereloot.reloot.RelootHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class RelootEntityCommand extends SubCommand {

    public RelootEntityCommand(BaseCommand parent) {
        super(parent, "relootEntity", null);
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        if(args.length != 1){
            sender.sendMessage(StructureReloot.PREFIX + "No Location provided");
            return true;
        }

        if(!(sender instanceof Player)) {
            sender.sendMessage(StructureReloot.PREFIX + "No Player");
            return true;
        }

        Player player = ((Player) sender);

        List<LootEntityValue> lootBlockValues = StructureReloot.getInstance().getDatabase(((Player) sender).getWorld()).getAllEntities();

        List<LootEntityValue> valid = lootBlockValues.stream()
                .filter(value -> value.getLocationString().equalsIgnoreCase(args[0]))
                .collect(Collectors.toList());

        RelootHelper.relootMultipleEntities(valid);

        if(valid.size() != 0){
            player.teleport(valid.get(0).getLocation());
        }

        sender.sendMessage(StructureReloot.PREFIX + "Relooted " + valid.size() + " entities");
        StructureReloot.getInstance().getDatabase(((Player) sender).getWorld()).close();
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return null;
        List<LootEntityValue> lootBlockValues = StructureReloot.getInstance().getDatabase(((Player) sender).getWorld()).getAllEntities(); //Not Performant i know

        return lootBlockValues.stream()
                .map(LootEntityValue::getLocationString)
                .filter(s -> s.startsWith(args[0]))
                .sorted()
                .collect(Collectors.toList());
    }
}
