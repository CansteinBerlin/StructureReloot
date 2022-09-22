package me.hasenzahn1.structurereloot.commands.reloot;

import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.autoupdate.RelootSettings;
import me.hasenzahn1.structurereloot.commandsystem.BaseCommand;
import me.hasenzahn1.structurereloot.commandsystem.SubCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.w3c.dom.Text;

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

        RelootSettings blockSettings = StructureReloot.getInstance().getBlockUpdateConfig().getSettingsForWorld(world);
        if(blockSettings == null){
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.info.noSettings", "world", world.getName()));
            return true;
        }

        RelootSettings entitySettings = StructureReloot.getInstance().getEntityUpdateConfig().getSettingsForWorld(world);
        if(entitySettings == null){
            sender.sendMessage(StructureReloot.PREFIX + StructureReloot.getLang("commands.info.noSettings", "world", world.getName()));
            return true;
        }

        TextComponent firstLine = new TextComponent(StructureReloot.PREFIX + StructureReloot.getLang("info.title"));
        BaseComponent[] titleLine = centerTextWithMinus(
                world.getName(),
                50,
                StructureReloot.getChatColor("info.minusColor"),
                StructureReloot.getChatColor("info.titleColor"));


        BaseComponent[] blockSettingsText = convertSettings(StructureReloot.getLang("info.blocks"), world, blockSettings);
        BaseComponent[] entitySettingsText = convertSettings(StructureReloot.getLang("info.entities"), world, entitySettings);

        sender.spigot().sendMessage(firstLine);
        sender.spigot().sendMessage(titleLine);
        sender.spigot().sendMessage(blockSettingsText);
        sender.spigot().sendMessage(new TextComponent("\n"));
        sender.spigot().sendMessage(entitySettingsText);
        sender.spigot().sendMessage(titleLine);
        return true;
    }

    private BaseComponent[] convertSettings(String type, World world, RelootSettings settings){
        TextComponent typeText = new TextComponent(type);

        BaseComponent[] relootOnStartup = combineComponents(
                textWithHover(new TextComponent(StructureReloot.getLang("info.relootOnStartup")), StructureReloot.getLang("info.relootOnStartupHover")),
                textWithCommand(textWithHover(new TextComponent(settings.isRelootOnStartup() ? "§ftrue" : "§8true"), StructureReloot.getLang("info.set")), "/reloot setRelootOnStartup " + world.getName() + " true"),
                new TextComponent("§7|"),
                textWithCommand(textWithHover(new TextComponent(!settings.isRelootOnStartup() ? "§ffalse" : "§8false"), StructureReloot.getLang("info.set")), "/reloot setRelootOnStartup " + world.getName() + " false")
        );

        BaseComponent[] maxRelootAmount = combineComponents(
                textWithHover(new TextComponent(StructureReloot.getLang("info.maxRelootAmount")), StructureReloot.getLang("info.maxRelootAmountHover")),
                textWithSuggestCommand(textWithHover(new TextComponent("§f" + (settings.getMaxRelootAmount() == Integer.MAX_VALUE ? "all" : settings.getMaxRelootAmount())), StructureReloot.getLang("info.set")), "/reloot setMaxRelootAmount ")
        );

        BaseComponent[] timeBetweenReloot = combineComponents(
                textWithHover(new TextComponent(StructureReloot.getLang("info.timeBetweenReloot")), StructureReloot.getLang("info.timeBetweenRelootHover")),
                textWithSuggestCommand(textWithHover(new TextComponent("§f" + settings.getDurationPattern()), StructureReloot.getLang("info.set")), "/reloot setDuration ")
        );

        return new ComponentBuilder(typeText)
                .retain(ComponentBuilder.FormatRetention.NONE)
                .append("\n", ComponentBuilder.FormatRetention.NONE)
                .append(relootOnStartup, ComponentBuilder.FormatRetention.NONE)
                .append("\n", ComponentBuilder.FormatRetention.NONE)
                .append(maxRelootAmount, ComponentBuilder.FormatRetention.NONE)
                .append("\n", ComponentBuilder.FormatRetention.NONE)
                .append(timeBetweenReloot, ComponentBuilder.FormatRetention.NONE)
                .create();
    }


    private BaseComponent[] centerTextWithMinus(String text, int width, ChatColor minusColor, ChatColor textColor){
        width -= text.length() - 2;
        return combineComponents(
                textWidthColor(new TextComponent("-".repeat(width / 2)), minusColor),
                textWidthColor(new TextComponent(text), textColor),
                textWidthColor(new TextComponent("-".repeat(width - width / 2)), minusColor));
    }

    private TextComponent textWithHover(TextComponent text, String subtitle){
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(subtitle)}));
        return text;
    }

    private TextComponent textWithCommand(TextComponent text, String command) {
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return text;
    }

    private TextComponent textWithSuggestCommand(TextComponent text, String command) {
        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        return text;
    }

    private TextComponent textWidthColor(TextComponent text, ChatColor color){
        text.setColor(color);
        return text;
    }

    private BaseComponent[] combineComponents(BaseComponent... components){
        ComponentBuilder componentBuilder = new ComponentBuilder().retain(ComponentBuilder.FormatRetention.NONE);
        for(BaseComponent c : components){
            componentBuilder.append(c, ComponentBuilder.FormatRetention.NONE).append("§r ", ComponentBuilder.FormatRetention.NONE);
        }
        return componentBuilder.create();
    }


}
