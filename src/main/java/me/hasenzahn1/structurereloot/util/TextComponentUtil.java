package me.hasenzahn1.structurereloot.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;

public class TextComponentUtil {

    public static BaseComponent[] centerTextWithMinus(String text, int width, ChatColor minusColor, ChatColor textColor){
        width -= text.length() - 2;
        return combineComponents(
                textWithColor(new TextComponent("-".repeat(width / 2)), minusColor),
                textWithColor(new TextComponent(text), textColor),
                textWithColor(new TextComponent("-".repeat(width - width / 2)), minusColor));
    }

    public static TextComponent textWithHover(TextComponent text, String subtitle){
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(subtitle)}));
        return text;
    }

    public static TextComponent textWithCommand(TextComponent text, String command) {
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return text;
    }

    public static TextComponent textWithSuggestCommand(TextComponent text, String command) {
        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        return text;
    }

    public static TextComponent textWithColor(TextComponent text, ChatColor color){
        text.setColor(color);
        return text;
    }

    public static BaseComponent[] combineComponents(BaseComponent... components){
        ComponentBuilder componentBuilder = new ComponentBuilder().retain(ComponentBuilder.FormatRetention.NONE);
        for(BaseComponent c : components){
            if(c == null) continue;
            componentBuilder.append(c, ComponentBuilder.FormatRetention.NONE).append("§r ", ComponentBuilder.FormatRetention.NONE);
        }
        return componentBuilder.create();
    }

}
