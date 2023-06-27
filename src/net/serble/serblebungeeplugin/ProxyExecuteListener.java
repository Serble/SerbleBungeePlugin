package net.serble.serblebungeeplugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class ProxyExecuteListener implements Listener {

    @EventHandler
    public void onMessage(final PluginMessageEvent e) {
        if (!e.getTag().equalsIgnoreCase("serble:proxyexecute")) {
            return;
        }

        final ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        final String subchannel = in.readUTF();
        if (!subchannel.equalsIgnoreCase("execute")) {
            return;
        }

        String playerUuid = in.readUTF();
        String command = in.readUTF();

        ProxiedPlayer player = Main.plugin.getProxy().getPlayer(UUID.fromString(playerUuid));
        if (player == null) {
            return;
        }

        ProxyServer.getInstance().getPluginManager().dispatchCommand(player, command);
    }

}
