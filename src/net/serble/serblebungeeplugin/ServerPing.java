package net.serble.serblebungeeplugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerPing implements Listener {

    @EventHandler
    public void onMessage(final PluginMessageEvent e) {

        if (!e.getTag().equalsIgnoreCase("serble:serverping")) {
            return;
        }

        final ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        final String subchannel = in.readUTF();
        if (subchannel.equalsIgnoreCase("ping")) {
            // get player arg
            final String player = in.readUTF();
            final String server = in.readUTF();

            ServerInfo target = ProxyServer.getInstance().getServerInfo(server);
            target.ping(ReceivedPing());

            // send response
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ping");
            out.writeUTF("STATUS");
            Main.plugin.getProxy().getPlayer(player).getServer().getInfo().sendData("serble:serverping", out.toByteArray());
        }

    }

    public Callback<net.md_5.bungee.api.ServerPing> ReceivedPing() {
        return (serverPing, throwable) -> {};
    }

}
