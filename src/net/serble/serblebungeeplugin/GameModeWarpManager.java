package net.serble.serblebungeeplugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.serble.serblebungeeplugin.Schemas.Config.GameMode;

import java.util.HashMap;
import java.util.UUID;

public class GameModeWarpManager implements Listener {
    private final HashMap<UUID, String> playerCurrentGameMode = new HashMap<>();

    @EventHandler
    public void onMessage(final PluginMessageEvent e) {
        if (!e.getTag().equalsIgnoreCase("serble:play")) {
            return;
        }

        final ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());

        final String subchannel = in.readUTF();
        if (!subchannel.equals("send")) return;  // This should never happen

        final String player = in.readUTF();
        final String gameMode = in.readUTF();
        final ProxiedPlayer p = ProxyServer.getInstance().getPlayer(UUID.fromString(player));
        if (p == null) return;  // They must have left or something

        sendPlayer(p, gameMode);
    }

    public void sendPlayer(ProxiedPlayer player, String gameMode) {
        // Get the gamemode with that name
        GameMode mode = Main.jsonConfig.GameModes.stream().filter(gm -> gm.Name.equalsIgnoreCase(gameMode)).findFirst().orElse(null);

        if (mode == null) {
            throw new IllegalArgumentException("Game mode '" + gameMode + "' does not exist");
        }

        playerCurrentGameMode.put(player.getUniqueId(), gameMode);

        ServerInfo server = ProxyServer.getInstance().getServerInfo(mode.Server);

        if (player.getServer() == null || !server.getName().equalsIgnoreCase(player.getServer().getInfo().getName())) {
            // Change server
            server.ping((result, error) -> {
                if (error != null) {
                    player.sendMessage(Utils.getMessage("&cThat game mode is currently unavailable"));
                    return;
                }

                if (result.getPlayers().getOnline() >= result.getPlayers().getMax()) {
                    player.sendMessage(Utils.getMessage("&cThat game mode is currently full"));
                    return;
                }

                sendPlayerChecksComplete(player, mode, true);
            });
        } else {
            sendPlayerChecksComplete(player, mode, false);
        }
    }

    private void sendPlayerChecksComplete(ProxiedPlayer player, GameMode gameMode, boolean changeServer) {
        ServerInfo server = ProxyServer.getInstance().getServerInfo(gameMode.Server);

        // Send the player to the server and then delay the world teleport
        if (changeServer) {
            player.connect(server);
            ProxyServer.getInstance().getScheduler().schedule(Main.plugin, () -> {
                boolean isOnTargetServer = player.getServer() != null && player.getServer().getInfo().getName().equalsIgnoreCase(server.getName());
                sendPlayerChecksComplete(player, gameMode, !isOnTargetServer);
            }, 1, java.util.concurrent.TimeUnit.SECONDS);
            return;
        }

        // Send the player to the world
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("world");
        out.writeUTF(player.getName());
        out.writeUTF(gameMode.World);
        player.getServer().getInfo().sendData("calcilator:svtp", out.toByteArray());
    }

    public String getPlayerCurrentGameMode(UUID player) {
        return playerCurrentGameMode.get(player);
    }

}
