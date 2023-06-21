package net.serble.serblebungeeplugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class GetConfig implements Listener {

    @EventHandler
    public void onMessage(final PluginMessageEvent e) {

        if (!e.getTag().equalsIgnoreCase("serble:serble")) {
            return;
        }

        final ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        final String subchannel = in.readUTF();
        if (subchannel.equalsIgnoreCase("config")) {
            // get player arg
            final String player = in.readUTF();

            // send config back to server
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("config");
            out.writeUTF(Main.content);
            Main.plugin.getProxy().getPlayer(player).getServer().getInfo().sendData("serble:serble", out.toByteArray());

            Main.plugin.getProxy().getLogger().info("Sent config data to a server");
        }

    }

}