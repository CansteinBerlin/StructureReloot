package me.hasenzahn1.structurereloot.commands.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RelootResetCommand extends SubCommand {

    public RelootResetCommand(BaseCommand parent) {
        super(parent, "reset", "reloot.commands.reset");
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        if(args.length != 2){
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.invalidCommand",
                    "command", getCommandHistory(),
                    "args", "<entity/block> " + worldString(tabComplete(null, new String[]{""})) ));
            return true;
        }
        World world = Bukkit.getWorld(args[1]);
        if(world == null){
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.reset.invalidWorld", "world", args[1]));
            return true;
        }

        if(args[0].equalsIgnoreCase("entity")){
            StructureReloot.getInstance().getDatabase(world).removeAllEntitys();
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.reset.removedEntities"));
        } else if (args[0].equalsIgnoreCase("block")) {
            StructureReloot.getInstance().getDatabase(world).removeAllBlocks();
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.reset.removedBlocks"));
        } else {
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.invalidCommand",
                    "command", getCommandHistory(),
                    "args", "<entity/block> " + worldString(tabComplete(null, new String[]{""}))));
        }
        return true;
    }

    private String worldString(List<String> worlds) {
        if(worlds.size() == 0) return "<>";
        StringBuilder sb = new StringBuilder("<" + worlds.get(0));
        for(int i = 1; i < worlds.size(); i++){
            sb.append("/").append(worlds.get(i));
        }
        sb.append(">");
        return sb.toString();
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1)
            return Arrays.stream(new String[]{"entity", "block"}).filter(s -> s.startsWith(args[0])).sorted().collect(Collectors.toList());
        if(args.length == 2)
            return Bukkit.getWorlds().stream()
                .map(World::getName)
                .filter(s -> s.startsWith(args[1]))
                .sorted()
                .collect(Collectors.toList());
        return new ArrayList<>();
    }
}
