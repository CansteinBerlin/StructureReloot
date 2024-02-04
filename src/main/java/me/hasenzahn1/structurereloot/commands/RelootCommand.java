package me.hasenzahn1.structurereloot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import de.themoep.minedown.MineDown;
import me.hasenzahn1.structurereloot.StructureReloot;
import me.hasenzahn1.structurereloot.config.LanguageConfig;
import me.hasenzahn1.structurereloot.config.UpdateConfig;
import me.hasenzahn1.structurereloot.database.LootBlockValue;
import me.hasenzahn1.structurereloot.database.LootEntityValue;
import me.hasenzahn1.structurereloot.database.LootValue;
import me.hasenzahn1.structurereloot.database.WorldDatabase;
import me.hasenzahn1.structurereloot.general.RelootSettings;
import me.hasenzahn1.structurereloot.reloot.RelootHelper;
import me.hasenzahn1.structurereloot.util.CommandUtils;
import me.hasenzahn1.structurereloot.util.TimeUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("reloot")
@CommandPermission("structurereloot.command.reloot")
public class RelootCommand extends BaseCommand {

    @HelpCommand
    @Syntax("")
    public static void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("info")
    @CommandPermission("structurereloot.command.info")
    @Syntax("(world)")
    public static void info(Player player, World world) {
        if (world == null) world = player.getWorld();

        //Gather Settings
        RelootSettings blockSettings = StructureReloot.getInstance().getBlockUpdateConfig().getSettingsForWorld(world);
        if (blockSettings == null) {
            player.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.info.noSettings", "world", world.getName()));
            return;
        }
        RelootSettings entitySettings = StructureReloot.getInstance().getEntityUpdateConfig().getSettingsForWorld(world);
        if (entitySettings == null) {
            player.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.info.noSettings", "world", world.getName()));
            return;
        }

        //Generate Titles and display to player
        TextComponent titleLine = new TextComponent(LanguageConfig.getLang("info.header"));
        BaseComponent[] worldLine = new MineDown(LanguageConfig.getLang("info.worldLine", "world", world.getName())).toComponent();

        //Get the converted settings
        BaseComponent[] blockSettingsText = CommandUtils.convertSettingsToStringInfo(LanguageConfig.getLang("info.blocks"), world, blockSettings, "block");
        BaseComponent[] entitySettingsText = CommandUtils.convertSettingsToStringInfo(LanguageConfig.getLang("info.entities"), world, entitySettings, "entity");

        //Send Messages to player
        player.spigot().sendMessage(titleLine);
        player.spigot().sendMessage(worldLine);
        player.spigot().sendMessage(blockSettingsText);
        player.spigot().sendMessage(new TextComponent(""));
        player.spigot().sendMessage(entitySettingsText);
        player.spigot().sendMessage(titleLine);
    }

    @Subcommand("listLootables")
    @CommandPermission("structurereloot.command.listLootables")
    @Syntax("<block/entity> (world) (page)")
    public static void listLootables(Player player, RelootSettings.Type type, World world, @Optional Integer page) {
        if (page == null || page < 0) page = 0;

        List<? extends LootValue> values = type == RelootSettings.Type.BLOCK ?
                StructureReloot.getInstance().getDatabaseManager().getDatabase(world).getAllBlocks() :
                StructureReloot.getInstance().getDatabaseManager().getDatabase(world).getAllEntities();

        CommandUtils.listLootables(player, world, page, values, type);
    }

    @Subcommand("regen")
    @CommandPermission("structurereloot.command.regen")
    @Syntax("<block/entity> <amount> (world)")
    public static void regen(Player player, RelootSettings.Type type, Integer amount, World world) {
        if (amount < 0) amount = Integer.MAX_VALUE;

        long millis = System.currentTimeMillis();
        Integer finalAmount = amount;
        if (type == RelootSettings.Type.BLOCK) {
            RelootHelper.regenNBlocks(world, amount, () ->
                    player.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.regen.sucBlock",
                            "amount", String.valueOf(finalAmount),
                            "time", ((System.currentTimeMillis() - millis) / 1000) + ""))
            );
        } else {
            RelootHelper.regenNEntities(world, amount, () -> {
                player.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.regen.sucEntity",
                        "amount", String.valueOf(finalAmount),
                        "time", ((System.currentTimeMillis() - millis) / 1000) + ""));
            });
        }
    }

    @Subcommand("reloadConfig")
    @CommandPermission("structurereloot.command.reloadConfig")
    @Syntax("<name> (override: true/false)")
    @CommandCompletion("@configName @boolean")
    public static void reloadConfig(Player player, String name, @Optional Boolean override) {
        if (override == null) override = false;

        StructureReloot instance = StructureReloot.getInstance();
        switch (name) {
            case "lang" -> {
                if (override) instance.getLanguageConfig().delete();
                instance.getLanguageConfig().reloadConfig();
            }
            case "config" -> {
                if (override) {
                    instance.getDefaultConfig().delete();
                    instance.setDefaultConfig(null);
                }
                instance.initDefaultConfig();
            }
            case "entityupdatesettings" -> {
                if (override) instance.getEntityUpdateConfig().delete();
                instance.setEntityUpdateConfig(new UpdateConfig(instance.getEntityUpdateConfig().getName()));
            }

            case "blockupdatesettings" -> {
                if (override) instance.getBlockUpdateConfig().delete();
                instance.setBlockUpdateConfig(new UpdateConfig(instance.getBlockUpdateConfig().getName()));
            }

            default -> {
                player.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.reloadConfig.invalidConfig",
                        "config", name));
                return;
            }
        }

        if (!override)
            player.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.reloadConfig.reloadSuccess",
                    "config", name));
        else
            player.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.reloadConfig.replaceSuccess",
                    "config", name));
    }

    @Subcommand("reset")
    @CommandPermission("structurereloot.command.reset")
    @Syntax("<block/entity> (world)")
    public static void reset(Player player, RelootSettings.Type type, World world) {
        if (type == RelootSettings.Type.BLOCK) {
            StructureReloot.getInstance().getDatabaseManager().getDatabase(world).removeAllEntitys();
            player.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.reset.removedEntities"));
        } else {
            StructureReloot.getInstance().getDatabaseManager().getDatabase(world).removeAllBlocks();
            player.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.reset.removedBlocks"));
        }
    }

    @Subcommand("internal")
    @Private
    @CommandPermission("structurereloot.command.internal")
    public class InternalCommand extends BaseCommand {

        @Subcommand("regen")
        @Syntax("<block/entity> <world> <locString>")
        public static void regen(Player player, RelootSettings.Type type, World world, String locString) {
            Location location = LootValue.getLocFromString(world, locString);
            WorldDatabase database = StructureReloot.getInstance().getDatabaseManager().getDatabase(world);
            int page;

            if (type == RelootSettings.Type.BLOCK) {
                LootBlockValue value = database.getBlock(location);
                if (value == null) return;
                List<LootBlockValue> values = database.getAllBlocks();
                page = (int) (Math.ceil(values.indexOf(value) / 10f) - 1);
                value.reloot();
                database.removeBlock(value);
            } else {
                LootEntityValue value = database.getEntity(location);
                if (value == null) return;
                List<LootEntityValue> values = database.getAllEntities();
                page = (int) (Math.ceil(values.indexOf(value) / 10f) - 1);
                value.reloot();
                database.removeEntity(value);
            }

            //Repromt menu
            RelootCommand.listLootables(player, type, world, page);
        }

        @Subcommand("remove")
        @Syntax("<block/entity> <world> <locString>")
        public static void remove(Player player, RelootSettings.Type type, World world, String locString) {
            WorldDatabase database = StructureReloot.getInstance().getDatabaseManager().getDatabase(world);
            int page;

            if (type == RelootSettings.Type.BLOCK) {
                LootBlockValue value = database.getBlock(LootValue.getLocFromString(world, locString));
                if (value == null) return;
                List<LootBlockValue> values = database.getAllBlocks();
                page = (int) (Math.ceil(values.indexOf(value) / 10f) - 1);
                database.removeBlock(value);
            } else {
                LootEntityValue value = database.getEntity(LootValue.getLocFromString(world, locString));
                if (value == null) return;
                List<LootEntityValue> values = database.getAllEntities();
                page = (int) (Math.ceil(values.indexOf(value) / 10f) - 1);
                database.removeEntity(value);
            }

            //Repromt Menu
            RelootCommand.listLootables(player, type, world, page);
        }

        @Subcommand("setDuration")
        @Syntax("<block/entity> <world> <pattern>")
        public static void setDuration(Player player, RelootSettings.Type type, World world, String arg) {
            RelootSettings settings = (type == RelootSettings.Type.BLOCK) ?
                    StructureReloot.getInstance().getBlockUpdateConfig().getSettingsForWorld(world) :
                    StructureReloot.getInstance().getEntityUpdateConfig().getSettingsForWorld(world);

            long amount = TimeUtil.parsePeriodToSeconds(arg);

            if (amount == 0) {
                player.sendMessage(StructureReloot.PREFIX + LanguageConfig.getLang("commands.settings.invalidPattern", "value", arg));
                return;
            }

            settings.setDurationPattern(arg);

            if (type == RelootSettings.Type.BLOCK) {
                StructureReloot.getInstance().getBlockUpdateConfig().update();
            } else {
                StructureReloot.getInstance().getEntityUpdateConfig().update();
            }

            //Redisplay Settings Menu
            RelootCommand.info(player, world);
        }

        @Subcommand("setMaxRelootAmount")
        public static void setMaxReloot(Player player, RelootSettings.Type type, World world, Integer amount) {
            RelootSettings settings = (type == RelootSettings.Type.BLOCK) ?
                    StructureReloot.getInstance().getBlockUpdateConfig().getSettingsForWorld(world) :
                    StructureReloot.getInstance().getEntityUpdateConfig().getSettingsForWorld(world);

            settings.setMaxRelootAmount(amount);

            if (type == RelootSettings.Type.BLOCK) {
                StructureReloot.getInstance().getBlockUpdateConfig().update();
            } else {
                StructureReloot.getInstance().getEntityUpdateConfig().update();
            }

            RelootCommand.info(player, world);
        }

        @Subcommand("setRelootOnStartup")
        public static void setRelootOnStartup(Player player, RelootSettings.Type type, World world, Boolean value) {
            RelootSettings settings = (type == RelootSettings.Type.BLOCK) ?
                    StructureReloot.getInstance().getBlockUpdateConfig().getSettingsForWorld(world) :
                    StructureReloot.getInstance().getEntityUpdateConfig().getSettingsForWorld(world);

            settings.setRelootOnStartup(value);

            if (type == RelootSettings.Type.BLOCK) {
                StructureReloot.getInstance().getBlockUpdateConfig().update();
            } else {
                StructureReloot.getInstance().getEntityUpdateConfig().update();
            }

            RelootCommand.info(player, world);
        }
    }
}
