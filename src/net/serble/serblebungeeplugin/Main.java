package net.serble.serblebungeeplugin;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.serble.serblebungeeplugin.Commands.PartyCommand;
import net.serble.serblebungeeplugin.Commands.SerbleReloadCommand;

import java.io.File;
import java.util.Scanner;

public class Main extends Plugin {

    public static Main plugin;
    private static PartyManager partyManager;
    private static PartyWarpManager partyWarpManager;
    public static boolean enabled = false;
    public static String content;

    @Override
    public void onEnable() {
        plugin = this;
        partyManager = new PartyManager();
        partyWarpManager = new PartyWarpManager();
        getProxy().registerChannel("serble:serble");
        getProxy().getPluginManager().registerListener(this, new GetConfig());
        getProxy().getPluginManager().registerListener(this, partyManager);

        getProxy().registerChannel("serble:party");
        getProxy().getPluginManager().registerListener(this, partyWarpManager);

        getProxy().registerChannel("serble:serverping");
        getProxy().getPluginManager().registerListener(this, new ServerPing());

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new SerbleReloadCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PartyCommand("party"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PartyCommand("p"));

        GetConfig();
    }

    @Override
    public void onDisable() {
        // Get a player for all connected spigot servers but only one for each
        // server. Then send a message to each server to disconnect all players
        // on that server.
        for (String server : ProxyServer.getInstance().getServers().keySet()) {
            ProxyServer.getInstance().getServerInfo(server).getPlayers().stream().findFirst().ifPresent(player -> {
                partyManager.sendResetNotification(player);
            });
        }
    }

    public static void GetConfig() {
        File config = new File(plugin.getDataFolder(), "config.json");
        StringBuilder contentB = new StringBuilder();

        try {
            Scanner myReader = new Scanner(config);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                contentB.append(data);
            }
            myReader.close();
        } catch (Exception error) {
            plugin.getLogger().severe("Couldn't get config.json file!");
            plugin.getLogger().severe("Disabling...");
            return;
        }

        content = contentB.toString();
        enabled = true;
    }

    public static PartyManager getPartyManager() {
        return partyManager;
    }

    public static PartyWarpManager getPartyWarpManager() {
        return partyWarpManager;
    }
}
