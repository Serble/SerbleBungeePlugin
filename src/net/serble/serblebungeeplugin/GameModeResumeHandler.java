package net.serble.serblebungeeplugin;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.serble.serblebungeeplugin.Schemas.Config.GameMode;

import java.io.IOException;
import java.util.Optional;

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
    public void onJoin(ServerConnectEvent e) {
        // Check if it is the first time joining
        if (e.getReason() != ServerConnectEvent.Reason.JOIN_PROXY) {
            return;
        }

        ProxiedPlayer p = e.getPlayer();

        if (!p.isConnected()) {
            Main.plugin.getLogger().info("Could not resume session for: " + p.getName() + " (Not connected)");
            return;
        }

        String previousGameMode = previousGameModes.getConfiguration().getString(p.getUniqueId().toString(), ConfigUtil.configuration.getString("hub-gamemode"));

        Optional<GameMode> prevGmObj = Main.jsonConfig.GameModes
                .stream()
                .filter(gm -> gm.Name.equalsIgnoreCase(previousGameMode)).findFirst();

        if (!prevGmObj.isPresent()) {
            Main.plugin.getLogger().severe("Could not resume session for: " + p.getName() + " (Game mode not found)");
            return;
        }

        ServerInfo targetServer = ProxyServer.getInstance().getServerInfo(prevGmObj.get().Server);
        e.setTarget(targetServer);

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
