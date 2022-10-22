package me.hasenzahn1.structurereloot.commands.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.commands.reloot.settings.SetMaxRelootCommand;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
import me.hasenzahn1.structurereloot.database.LootValue;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;
import org.bukkit.loot.LootTable;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.hasenzahn1.structurereloot.util.TextComponentUtil.*;

public class RelootListLootablesCommand extends SubCommand {


    public RelootListLootablesCommand(BaseCommand parent) {
        super(parent, "listLootables", null); // "reloot.commands.listLootables"
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        if(args.length > 3 || args.length == 0){
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.invalidCommand",
                    "command", getCommandHistory(),
                    "args", "<block/entity> <world> <page>"));
            return true;
        }

        if(!args[0].equalsIgnoreCase("block") && !args[0].equalsIgnoreCase("entity")){
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.invalidCommand",
                    "command", getCommandHistory(),
                    "args", "<block/entity> <world> <page>"));
            return true;
        }

        World world;
        if(args.length == 1){
            if(!(sender instanceof Player)){
                sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.noPlayer"));
                return true;
            }
            world = ((Player) sender).getWorld();
        }else{
            world = Bukkit.getWorld(args[1]);
            if(world == null){
                sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.reset.invalidWorld", "world", args[1]));
                return true;
            }
        }

        int page = 0;
        if(args.length == 3){
            if(!SetMaxRelootCommand.isInt(args[2])){
                sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.invalidCommand",
                        "command", getCommandHistory(),
                        "args", "<block/entity> <world> <page>"));
                return true;
            }else{
                page = Math.max(Integer.parseInt(args[2]) - 1, 0);
            }
        }

        List<? extends LootValue> values = args[0].equalsIgnoreCase("block") ?
                StructureReloot.getInstance().getDatabase(world).getAllBlocks() :
                StructureReloot.getInstance().getDatabase(world).getAllEntities();

        StructureReloot.getInstance().getDatabase(world).close();
        listAllElements(sender, world, page, values, args[0].equalsIgnoreCase("block") ? "blocks" : "entities");

        return true;
    }

    public static void listAllElements(CommandSender sender, World world, int page, List<? extends LootValue> values, String entityType){

        if(values.size() == 0){
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.listlootables.noLootables", "type", entityType, "world", world.getName()));
            return;
        }
        //Get Chatcolor from config
        ChatColor minusColor = StructureReloot.getChatColor("info.minusColor");
        ChatColor titleColor = StructureReloot.getChatColor("info.titleColor");

        //First line with Prefix
        TextComponent firstLine = new TextComponent(StructureReloot.PREFIX + StructureReloot.getLang("listLootTables.title", "type", entityType));
        sender.spigot().sendMessage(firstLine);

        //Title Line ---- world ---
        BaseComponent[] titleLine = centerTextWithMinus(
                world.getName(),
                50,
                minusColor,
                titleColor);
        sender.spigot().sendMessage(titleLine);

        //For Every element in "page"
        for(int i = page * 10; i < Math.min((page + 1) * 10, values.size()); i++){ //Loop through all elements on "page"
            String lootTable = values.get(i).getLootTable() == null ? "Item Frame" : getNameFromLootTable(values.get(i).getLootTable());
            Location loc = values.get(i).getLocation();
            String locString = values.get(i).getLocationString();

            //Element text <lootTable> (<loc>) [Reloot if perm] [x if perm]
            BaseComponent[] comps = combineComponents(
                new TextComponent("§6  " + lootTable),
                    textWithHover(textWithCommand(new TextComponent("§8(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")"), sender.hasPermission("minecraft.command.teleport") ? "/minecraft:tp " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() : ""), sender.hasPermission("minecraft.command.teleport") ? StructureReloot.getLang("listLootTables.teleport") : ""),
                    sender.hasPermission("reloot.commands.regen") ? textWithCommand(new TextComponent(StructureReloot.getLang("listLootTables.reloot")),
                            "/reloot internal regen " + world.getName() + " " + (entityType.equalsIgnoreCase("blocks") ? "block" : "entity") + " " + locString) : null,
                    sender.hasPermission("reloot.commands.reset") ? textWithCommand(new TextComponent("§c[x]"),
                            "/reloot internal remove " + world.getName() + " " + (entityType.equalsIgnoreCase("blocks") ? "block" : "entity") + " " + locString) : null
            );
            sender.spigot().sendMessage(comps);
        }

        //Bottom Line with "page selection"
        int amount = 58 - 18 - ("" + page).length() - ("" + (int) Math.ceil(values.size() / 10f)).length();
        if(page > 0 && page < ((int) Math.ceil(values.size() / 10f)) - 1) amount -= 2;
        BaseComponent[] comps = combineComponents(
                textWithColor(new TextComponent("-".repeat((int) Math.floor(amount / 2f))), minusColor),
                page > 0 ? textWithCommand(textWithColor(new TextComponent("<<<"), titleColor),
                        "/reloot listLootables " + (entityType.equalsIgnoreCase("blocks") ? "block" : "entity") + " " + world.getName() + " " + (page)) : textWithColor(new TextComponent("|||"), titleColor),
                textWithColor(new TextComponent("Page"), titleColor),
                textWithColor(new TextComponent("" + (page + 1)), titleColor),
                textWithColor(new TextComponent("/"), titleColor),
                textWithColor(new TextComponent("" + ((int) Math.ceil(values.size() / 10f))), titleColor),
                page < ((int) Math.ceil(values.size() / 10f)) - 1 ? textWithCommand(textWithColor(new TextComponent(">>>"), titleColor),
                        "/reloot listLootables " + (entityType.equalsIgnoreCase("blocks") ? "block" : "entity") + " " + world.getName() + " " + (page+2)) : textWithColor(new TextComponent("|||"), titleColor),
                textWithColor(new TextComponent("-".repeat((int) Math.ceil(amount / 2f))), minusColor)
        );
        sender.spigot().sendMessage(comps);

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1){
            return Arrays.asList(new String[]{"block", "entity"}).stream().filter(s -> s.startsWith(args[0])).sorted().collect(Collectors.toList());
        }
        if(args.length == 2){
            return Bukkit.getWorlds().stream().map(World::getName).filter(s -> s.startsWith(args[0])).sorted().collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    private static String getNameFromLootTable(LootTable lootTable){
        String s = lootTable.getKey().getKey();
        String[] splits = s.split("/");
        String name = splits[splits.length - 1];
        return toTitleCase(name.replace("_", " "));
    }

    private static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String s : arr) {
            sb.append(Character.toUpperCase(s.charAt(0)))
                    .append(s.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
