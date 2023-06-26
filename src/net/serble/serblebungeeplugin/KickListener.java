package net.serble.serblebungeeplugin;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.List;

public class KickListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerKickEvent(ServerKickEvent e) {
        ServerInfo fallback = ProxyServer.getInstance().getServerInfo(ConfigUtil.configuration.getString("fallback-server"));
        if (fallback == null) {
            Main.plugin.getLogger().severe("Unable to find the specified fallback server!!");
            return;
        }

        String reason = BaseComponent.toLegacyText(e.getKickReasonComponent());

        List<String> reasonList = ConfigUtil.configuration.getStringList("ignore-fallback-keywords");
        for (String s : reasonList) {
            if (reason.toLowerCase().contains(s.toLowerCase())) {
                return;  // Don't do anything if the reason contains a keyword
            }
        }

        String from = e.getKickedFrom().getName();
        e.setCancelServer(fallback);
        e.setCancelled(true);
        e.getPlayer().sendMessage(Utils.getMessage(reason));
        e.getPlayer().sendMessage(Utils.getMessage("&cYour connection to &7" + from + "&c was interrupted. You have been connected to: &7" + fallback.getName()));
    }

}
