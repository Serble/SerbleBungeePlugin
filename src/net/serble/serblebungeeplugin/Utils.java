package net.serble.serblebungeeplugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;

public class Utils {

    public static BaseComponent[] getMessage(String... strings) {
        BaseComponent[] message = new BaseComponent[strings.length];
        for (int i = 0; i < strings.length; i++) {
            message[i] = new net.md_5.bungee.api.chat.TextComponent(ChatColor.translateAlternateColorCodes('&', strings[i]));
        }
        return message;
    }

    public static BaseComponent[] getMessage(BaseComponent... components) {
        return components;
    }

    public static String colours(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
