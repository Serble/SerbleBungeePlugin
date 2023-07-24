package net.serble.serblebungeeplugin;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatManager implements Listener {

    public ChatManager() {

    }

    public void sendBroadcast(String message) {
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            p.sendMessage(Utils.getMessage(message));
        }
    }

    @EventHandler
    public void onPlayerChat(ChatEvent e) {
        if (e.isCommand()) {
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        String message = e.getMessage();
        if (p.hasPermission("serble.chatcolours")) {
            message = Utils.colours(message);
        }

        e.setCancelled(true);

    }

    @EventHandler
    public void onChatAsync(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        Bukkit.getScheduler().callSyncMethod(Main.plugin, () -> {
            AchievementsManager.GrantAchievementProgress(p, Achievement.I_HAVE_A_VOICE);
            return null;
        });

        if (!Main.hasConfig) {
            // get config
            ConfigManager.requestConfig(p);
            e.setCancelled(true);
            ConfigManager.runTaskAfterConfig(() -> fakeChat(p, e.getMessage()));
            return;
        }

        String format = getFormat(p);
        if (p.hasPermission("serble.chatcolours")) e.setMessage(Functions.translate(e.getMessage()));

        String finalMessage = Functions.translate(format);


        e.setFormat(finalMessage);
    }

    public static String getFormat(ProxiedPlayer p) {
        // Get what rank should be displayed
        String rankDisplay = Functions.getPlayerRankDisplay(p);

        // Get what gamemode should be displayed
        String worldDisplay = Main.getGameModeWarpManager().getPlayerCurrentGameMode(p.getUniqueId());

        String format = Main.jsonConfig.ChatFormat;
        format = format.replaceAll("\\{world}", worldDisplay);
        format = format.replaceAll("\\{rank}", rankDisplay);
        format = format.replaceAll("\\{level}", "0");
        format = format.replaceAll("\\{name}", "%s");
        format = format.replaceAll("\\{message}", "%s");
        return format;
    }

}
