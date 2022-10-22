package me.hasenzahn1.structurereloot.commands.reloot;

import de.themoep.minedown.MineDown;
import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.general.RelootSettings;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static me.hasenzahn1.structurereloot.util.TextComponentUtil.*;

public class RelootInfoCommand extends SubCommand {

    public RelootInfoCommand(BaseCommand parent) {
        super(parent, "info", "reloot.commands.info");
    }

    @Override
    public boolean performCommand(CommandSender sender, String[] args) {
        if(args.length > 1){
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.invalidCommand"),
                    "command", getCommandHistory(),
                    "args", "<world>");
            return true;
        }

        World world = null;
        if(args.length == 0){
            if(!(sender instanceof Player)){
                sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.noPlayer"));
                return true;
            }
            world = ((Player) sender).getWorld();
        }
        else {
            world = Bukkit.getWorld(args[0]);
            if(world == null){
                sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.reset.invalidWorld", "world", args[0]));
                return true;
            }
        }

        sendPlayerInfoScreen(sender, world);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Bukkit.getWorlds().stream()
                .map(World::getName)
                .filter(s -> s.startsWith(args[0]))
                .sorted()
                .collect(Collectors.toList());
    }

    public static void sendPlayerInfoScreen(CommandSender sender, World world){
        RelootSettings blockSettings = StructureReloot.getInstance().getBlockUpdateConfig().getSettingsForWorld(world);
        if(blockSettings == null){
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.info.noSettings", "world", world.getName()));
            return;
        }

        RelootSettings entitySettings = StructureReloot.getInstance().getEntityUpdateConfig().getSettingsForWorld(world);
        if(entitySettings == null){
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.info.noSettings", "world", world.getName()));
            return;
        }

        TextComponent titleLine = new TextComponent(StructureReloot.getLang("info.header"));
        BaseComponent[] worldLine = new MineDown(StructureReloot.getLang("info.worldLine", "world", world.getName())).toComponent();


        BaseComponent[] blockSettingsText = convertSettings(StructureReloot.getLang("info.blocks"), world, blockSettings, "block");
        BaseComponent[] entitySettingsText = convertSettings(StructureReloot.getLang("info.entities"), world, entitySettings, "entity");

        //sender.spigot().sendMessage(firstLine);
        sender.spigot().sendMessage(titleLine);
        sender.spigot().sendMessage(worldLine);
        sender.spigot().sendMessage(blockSettingsText);
        sender.spigot().sendMessage(new TextComponent(""));
        sender.spigot().sendMessage(entitySettingsText);
        sender.spigot().sendMessage(titleLine);
    }

    private static BaseComponent[] convertSettings(String type, World world, RelootSettings settings, String commandType){
        TextComponent typeText = new TextComponent(type);

        BaseComponent[] relootOnStartup = new MineDown(StructureReloot.getLang("info.relootOnStartupLine",

                "relootOnStartup", StructureReloot.getLang("info.relootOnStartup"),
                "relootOnStartupHover", StructureReloot.getLang("info.relootOnStartupHover"),
                "colorTrue", settings.isRelootOnStartup() ? "white" : "dark_gray",
                "colorFalse", settings.isRelootOnStartup() ? "dark_gray" : "white",
                "setHover", StructureReloot.getLang("info.setHover"),
                "commandType", commandType,
                "world", world.getName())
        ).toComponent();



        BaseComponent[] maxRelootAmount = new MineDown(StructureReloot.getLang("info.setMaxAmountLine",
                "maxRelootAmount", StructureReloot.getLang("info.maxRelootAmount"),
                "maxRelootAmountHover", StructureReloot.getLang("info.maxRelootAmountHover"),
                "amount", settings.getMaxRelootAmount() == Integer.MAX_VALUE ? "all" : settings.getMaxRelootAmount() + "",
                "commandType", commandType,
                "world", world.getName(),
                "setHover", StructureReloot.getLang("info.setHover"))
        ).toComponent();

        BaseComponent[] timeBetweenReloot = new MineDown(StructureReloot.getLang("info.timeBetweenRelootLine",
                "timeBetweenReloot", StructureReloot.getLang("info.timeBetweenReloot"),
                "timeBetweenRelootHover", StructureReloot.getLang("info.timeBetweenRelootHover"),
                "time", settings.getDurationPattern(),
                "commandType", commandType,
                "world", world.getName(),
                "setHover", StructureReloot.getLang("info.setHover"))
        ).toComponent();


        return new ComponentBuilder()
                .retain(ComponentBuilder.FormatRetention.NONE)
                .append(typeText)
                .append("\n")
                .append(relootOnStartup)
                .append("\n")
                .append(maxRelootAmount)
                .append("\n")
                .append(timeBetweenReloot)
                .create();
    }
}
