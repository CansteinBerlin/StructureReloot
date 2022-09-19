package me.hasenzahn1.structurereloot.commands.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
import me.hasenzahn1.structurereloot.database.WorldDatabase;
import me.hasenzahn1.structurereloot.databasesystem.Database;
import me.hasenzahn1.structurereloot.reloot.RelootHelper;
import me.hasenzahn1.structurereloot.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Bat;

import java.util.*;
import java.util.stream.Collectors;

public class RelootRegenCommand extends SubCommand { //TODO: Test

    public RelootRegenCommand(BaseCommand parent) {
        super(parent, "regen", "reloot.commands.regen");
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        if(args.length != 3){
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.invalidCommand",
                    "command", getCommandHistory(),
                    "args", StringUtils.listToCommandArgs(tabComplete(null, new String[]{""})) +
                            " " +
                            StringUtils.listToCommandArgs(tabComplete(null, new String[]{"", ""})) +
                            " " +
                            "<1/2/.../all>"
                    ));

            return true;
        }

        //Check World
        World world = Bukkit.getWorld(args[1]);
        if(world == null){
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.reset.invalidWorld", "world", args[1]));
            return true;
        }

        //Check Amount
        int amount;
        if(args[2].equalsIgnoreCase("all")){
            amount = Integer.MAX_VALUE;
        }else{
            try{
                amount = Integer.parseInt(args[2]);
            }catch (NumberFormatException e){
                sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.invalidCommand",
                        "command", getCommandHistory(),
                        "args", StringUtils.listToCommandArgs(tabComplete(null, new String[]{""})) +
                                " " +
                                StringUtils.listToCommandArgs(tabComplete(null, new String[]{"", ""})) +
                                " " +
                                "<1/2/.../all>"
                ));
                return true;
            }
        }


        if(args[0].equalsIgnoreCase("entity")){
            regenEntities(world, amount);

            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.regen.sucEntity", "amount", args[1].toLowerCase(Locale.ROOT)));

        } else if (args[0].equalsIgnoreCase("block")) {
            regenBlocks(world, amount);

            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.regen.sucBlock", "amount", args[1].toLowerCase(Locale.ROOT)));

        } else if(args[0].equalsIgnoreCase("all")){
            regenEntities(world, amount);
            regenBlocks(world, amount);

            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.regen.sucBoth", "amount", args[0].toLowerCase(Locale.ROOT)));
        } else {
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.invalidCommand",
                    "command", getCommandHistory(),
                    "args", StringUtils.listToCommandArgs(tabComplete(null, new String[]{""})) +
                            " " +
                            StringUtils.listToCommandArgs(tabComplete(null, new String[]{"", ""})) +
                            " " +
                            "<1/2/.../all>"
            ));
        }
        StructureReloot.getInstance().getDatabase(world).close();
        return true;
    }

    public void regenEntities(World world, int amount){
        List<LootEntityValue> levs = StructureReloot.getInstance().getDatabase(world).getAllEntities();
        Collections.shuffle(levs);
        List<LootEntityValue> values = levs.stream().limit(Math.min(levs.size(), amount)).collect(Collectors.toList());

        WorldDatabase database = StructureReloot.getInstance().getDatabase(world);
        database.setCacheRemove(true);
        RelootHelper.relootMultipleEntities(values);
        database.removeLootEntityValues(values);
        database.setCacheRemove(false);
    }

    public void regenBlocks(World world, int amount){
        List<LootBlockValue> lbvs = StructureReloot.getInstance().getDatabase(world).getAllBlocks();
        Collections.shuffle(lbvs);
        List<LootBlockValue> values = lbvs.stream().limit(Math.min(lbvs.size(), amount)).collect(Collectors.toList());

        //Bukkit.broadcastMessage(values.stream().map(LootBlockValue::getLocationString).collect(Collectors.toList()) + "");

        WorldDatabase database = StructureReloot.getInstance().getDatabase(world);
        database.setCacheRemove(true);
        RelootHelper.relootMultipleBlocks(values);
        database.removeLootBlockValues(values);
        database.setCacheRemove(false);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1){
            return Arrays.stream(new String[]{"entity", "block", "all"})
                    .filter(s -> s.startsWith(args[0]))
                    .sorted()
                    .collect(Collectors.toList());
        }
        if(args.length == 2){
            return Bukkit.getWorlds().stream()
                    .map(World::getName)
                    .filter(s -> s.startsWith(args[1]))
                    .sorted()
                    .collect(Collectors.toList());
        }
        if(args.length == 3){
            return Arrays.stream(new String[]{"...", "1", "2", "all", "3"})
                    .filter(s -> s.startsWith(args[2]))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
