package me.hasenzahn1.structurereloot.util;

import de.themoep.minedown.MineDown;
import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.config.LanguageConfig;
import me.hasenzahn1.structurereloot.database.LootValue;
import me.hasenzahn1.structurereloot.general.RelootSettings;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;

import java.util.List;

import static me.hasenzahn1.structurereloot.util.TextComponentUtil.*;

public class CommandUtils {

    public static String teleportPermission;
    public static String teleportCommand;

    public static BaseComponent[] convertSettingsToStringInfo(String type, World world, RelootSettings settings, String commandType) {
        TextComponent typeText = new TextComponent(type);
        BaseComponent[] relootOnStartup = new MineDown(LanguageConfig.getLang("info.relootOnStartupLine",
                "relootOnStartup", LanguageConfig.getLang("info.relootOnStartup"),
                "relootOnStartupHover", LanguageConfig.getLang("info.relootOnStartupHover"),
                "colorTrue", settings.isRelootOnStartup() ? "white" : "dark_gray",
                "colorFalse", settings.isRelootOnStartup() ? "dark_gray" : "white",
                "setHover", LanguageConfig.getLang("info.setHover"),
                "commandType", commandType,
                "world", world.getName())
        ).toComponent();

        BaseComponent[] maxRelootAmount = new MineDown(LanguageConfig.getLang("info.setMaxAmountLine",
                "maxRelootAmount", LanguageConfig.getLang("info.maxRelootAmount"),
                "maxRelootAmountHover", LanguageConfig.getLang("info.maxRelootAmountHover"),
                "amount", settings.getMaxRelootAmount() == Integer.MAX_VALUE ? "all" : settings.getMaxRelootAmount() + "",
                "commandType", commandType,
                "world", world.getName(),
                "setHover", LanguageConfig.getLang("info.setHover"))
        ).toComponent();

        BaseComponent[] timeBetweenReloot = new MineDown(LanguageConfig.getLang("info.timeBetweenRelootLine",
                "timeBetweenReloot", LanguageConfig.getLang("info.timeBetweenReloot"),
                "timeBetweenRelootHover", LanguageConfig.getLang("info.timeBetweenRelootHover"),
                "time", settings.getDurationPattern(),
                "commandType", commandType,
                "world", world.getName(),
                "setHover", LanguageConfig.getLang("info.setHover"))
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

    public static void listLootables(Player player, World world, int page, List<? extends LootValue> values, RelootSettings.Type type) {
        String entityType = type == RelootSettings.Type.BLOCK ? "blocks" : "entities";
        if (values.isEmpty()) {
            player.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.listlootables.noLootables",
                    "type", entityType,
                    "world", world.getName()));
            return;
        }

        //Get Chatcolor from config
        ChatColor minusColor = LanguageConfig.getChatColor("info.minusColor");
        ChatColor titleColor = LanguageConfig.getChatColor("info.titleColor");

        //First line with Prefix
        TextComponent firstLine = new TextComponent(StructureReloot.PREFIX + LanguageConfig.getLang("listLootTables.title", "type", entityType));
        player.spigot().sendMessage(firstLine);

        //Title Line ---- world ---
        BaseComponent[] titleLine = centerTextWithMinus(
                world.getName(),
                50,
                minusColor,
                titleColor);
        player.spigot().sendMessage(titleLine);

        //Generate the main Text body
        for (int i = page * 10; i < Math.min((page + 1) * 10, values.size()); i++) { //Loop through all elements on "page"
            String lootTable = values.get(i).getLootTable() == null ? "Item Frame" : getNameFromLootTable(values.get(i).getLootTable());
            Location loc = values.get(i).getLocation();
            String locString = values.get(i).getLocationString();


            //Element text <lootTable> (<loc>) [Reloot if perm] [x if perm]
            BaseComponent[] comps = combineComponents(
                    new TextComponent("§6  " + lootTable),
                    textWithHover(
                            textWithCommand(
                                    new TextComponent("§8(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")"),
                                    player.hasPermission(teleportPermission) ? teleportCommand
                                            .replaceAll("%x%", String.valueOf(loc.getBlockX()))
                                            .replaceAll("%y%", String.valueOf(loc.getBlockY()))
                                            .replaceAll("%z%", String.valueOf(loc.getBlockZ()))
                                            .replaceAll("%world%", loc.getWorld().getName())
                                            : ""), player.hasPermission(teleportPermission) ? LanguageConfig.getLang("listLootTables.teleport") : ""),

                    player.hasPermission("structurereloot.command.regen") ?
                            textWithCommand(new TextComponent(LanguageConfig.getLang("listLootTables.reloot")),
                                    "/reloot internal regen " + type + " " + world.getName() + " " + locString)
                            : null,

                    player.hasPermission("structurereloot.command.reset") ?
                            textWithCommand(new TextComponent("§c[x]"),
                                    "/reloot internal remove " + type + " " + world.getName() + " " + locString)
                            : null
            );
            player.spigot().sendMessage(comps);
        }

        int amount = 58 - 18 - ("" + page).length() - ("" + (int) Math.ceil(values.size() / 10f)).length();
        if (page > 0 && page < ((int) Math.ceil(values.size() / 10f)) - 1) amount -= 2;
        BaseComponent[] comps = combineComponents(
                textWithColor(new TextComponent("-".repeat((int) Math.floor(amount / 2f))), minusColor),
                page > 0 ? textWithCommand(textWithColor(new TextComponent("<<<"), titleColor),
                        "/reloot listLootables " + type + " " + world.getName() + " " + (page - 1)) : textWithColor(new TextComponent("|||"), titleColor),
                textWithColor(new TextComponent("Page"), titleColor),
                textWithColor(new TextComponent("" + (page + 1)), titleColor),
                textWithColor(new TextComponent("/"), titleColor),
                textWithColor(new TextComponent("" + ((int) Math.ceil(values.size() / 10f))), titleColor),
                page < ((int) Math.ceil(values.size() / 10f)) - 1 ? textWithCommand(textWithColor(new TextComponent(">>>"), titleColor),
                        "/reloot listLootables " + type + " " + world.getName() + " " + (page + 1)) : textWithColor(new TextComponent("|||"), titleColor),
                textWithColor(new TextComponent("-".repeat((int) Math.ceil(amount / 2f))), minusColor)
        );
        player.spigot().sendMessage(comps);

    }

    private static String getNameFromLootTable(LootTable lootTable) {
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

    public static boolean isInt(String s) {
        try {

            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
