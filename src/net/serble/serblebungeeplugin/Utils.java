package net.serble.serblebungeeplugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.serble.serblebungeeplugin.Schemas.Config.Rank;

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

    public static String getPlayerRankDisplay(ProxiedPlayer p, boolean ignoreNick) {
        if (!ignoreNick) {
            String rankName = NicknameManager.getRankNick(p.getUniqueId());

            if (rankName != null) {
                for (int i = 0; i < Main.config.Ranks.size(); i++) {
                    Rank rank = Main.config.Ranks.get(i);
                    if (rank.Name.equalsIgnoreCase(rankName)) {
                        return rank.Display;
                    }
                }
            }
        }

        String rankDisplay = "&1[&2Error Getting Rank&1]&2";

        for (int i = 0; i < Main.jsonConfig.Ranks.size(); i++) {
            Rank rank = Main.jsonConfig.Ranks.get(i);
            if (p.hasPermission(rank.Permission)) {
                rankDisplay = rank.Display;
            }
        }

        return rankDisplay;
    }

}
