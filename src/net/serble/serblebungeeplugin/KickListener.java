package net.serble.serblebungeeplugin;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.List;
import java.util.Objects;

public class KickListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerKickEvent(ServerKickEvent e) {
        if (!ConfigUtil.configuration.getBoolean("kick-listener")) {
            return;
        }

        if (e.getPlayer() == null || !e.getPlayer().isConnected()) {
            ProxyServer.getInstance().getLogger().info("Player disconnected");
            return;
        }

        ServerInfo fallback = ProxyServer.getInstance().getServerInfo(ConfigUtil.configuration.getString("fallback-server"));
        if (fallback == null) {
            Main.plugin.getLogger().severe("Unable to find the specified fallback server!!");
            return;
        }

        String reason = BaseComponent.toLegacyText(e.getKickReasonComponent());

        List<String> reasonList = ConfigUtil.configuration.getStringList("ignore-fallback-keywords");
        for (String s : reasonList) {
            if (reason.toLowerCase().contains(s.toLowerCase()) || reason.contains("FORCE_DC")) {
                return;  // Don't do anything if the reason contains a keyword
            }
        }

        final String from = e.getKickedFrom().getName();

        final boolean isLimbo = Objects.equals(from, fallback.getName());
        if (Objects.equals(from, fallback.getName())) {
            fallback = ProxyServer.getInstance().getServerInfo(ConfigUtil.configuration.getString("limbo-server"));
        }

        e.setCancelServer(fallback);
        e.setCancelled(true);
        e.getPlayer().sendMessage(Utils.getMessage(reason));
        e.getPlayer().sendMessage(Utils.getMessage("&cYour connection to &7" + from + "&c was interrupted. You have been connected to: &7" + fallback.getName()));

        final String fallbackName = fallback.getName();
        fallback.ping((ping, error) -> {
            if (error == null) {
                return;
            }

            if (isLimbo) {
                e.getPlayer().disconnect(Utils.getMessage("&cYour connection to &7" + from + "&c was interrupted and no other valid servers were found. You have been disconnected. &7ERROR: LIMBO_ERROR, FORCE_DC"));
                return;
            }
            ServerInfo limbo = ProxyServer.getInstance().getServerInfo(ConfigUtil.configuration.getString("limbo-server"));
            e.getPlayer().sendMessage(Utils.getMessage("&7" + fallbackName + "&c is currently offline. You have been connected to: &7" + limbo.getName()));
            e.getPlayer().connect(limbo);
        });
    }

}
