package net.serble.serblebungeeplugin;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;

public class GameModeResumeHandler implements Listener {
    private final YmlConfig previousGameModes;

    public GameModeResumeHandler() {
        try {
            previousGameModes = new YmlConfig("previousGameModes.yml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onJoin(PostLoginEvent e) {
        ProxiedPlayer p = e.getPlayer();

        String previousGameMode = previousGameModes.getConfiguration().getString(p.getUniqueId().toString(), ConfigUtil.configuration.getString("hub-gamemode"));
        Main.getGameModeWarpManager().sendPlayer(p, previousGameMode);
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent e) {
        ProxiedPlayer p = e.getPlayer();

        String currentGameMode = Main.getGameModeWarpManager().getPlayerCurrentGameMode(p.getUniqueId());
        if (currentGameMode == null) {
            return;
        }

        previousGameModes.getConfiguration().set(p.getUniqueId().toString(), currentGameMode);
        try {
            save();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void save() throws IOException {
        previousGameModes.save();
    }

}
