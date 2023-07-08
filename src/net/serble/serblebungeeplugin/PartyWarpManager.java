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
import net.serble.serblebungeeplugin.Schemas.Party;

import java.util.Objects;
import java.util.UUID;

public class PartyWarpManager implements Listener {

    public void warp(Party party, boolean fullWarp, boolean delayed) {
        if (delayed) {
            // Delayed warp
            ProxyServer.getInstance().getScheduler().schedule(Main.plugin, () -> warp(party, fullWarp, false), 1, java.util.concurrent.TimeUnit.SECONDS);
            return;
        }

        ServerInfo server = ProxyServer.getInstance().getPlayer(party.getLeader()).getServer().getInfo();
        for (UUID memberId : party.getMembers()) {
            ProxiedPlayer member = ProxyServer.getInstance().getPlayer(memberId);
            if (member == null) {
                continue;
            }

            // Send player to same server as leader
            if (!Objects.equals(member.getServer().getInfo().getName(), server.getName())) member.connect(server);

            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(fullWarp ? "fullwarp" : "warp");  // It's a warp request
            out.writeUTF(party.getLeader().toString());  // Send leader UUID
            out.writeUTF(memberId.toString());
            server.sendData("serble:party", out.toByteArray());  // Send the warp to the leaders server, so they can await the warped player connecting
        }
    }

    @EventHandler
    public void onMessage(final PluginMessageEvent e) {

        if (!e.getTag().equalsIgnoreCase("serble:party")) {
            return;
        }

        final ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        final String subchannel = in.readUTF();
        if (!subchannel.equals("warp") && !subchannel.equals("delayedwarp")) return;  // This should never happen
        boolean delayed = subchannel.equals("delayedwarp");
        final String player = in.readUTF();

        final ProxiedPlayer p = ProxyServer.getInstance().getPlayer(UUID.fromString(player));
        if (p == null) return;  // They must have left or something

        Party party = Main.getPartyManager().getParty(p);
        if (party == null) return;  // They must have disbanded the party or maybe something broke, probably not important

        warp(party, false, delayed);
    }

}
