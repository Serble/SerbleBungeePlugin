package net.serble.serblebungeeplugin;

import com.google.gson.Gson;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.serble.serblebungeeplugin.Commands.*;
import net.serble.serblebungeeplugin.Schemas.Config.ConfigSave;

import java.io.File;
import java.util.Scanner;

public class Main extends Plugin {

    public static Main plugin;
    private static PartyManager partyManager;
    private static PartyWarpManager partyWarpManager;
    private static GameModeWarpManager gameModeWarpManager;
    public static boolean enabled = false;
    public static String content;
    public static ConfigSave jsonConfig;

    @Override
    public void onEnable() {
        plugin = this;
        partyManager = new PartyManager();
        partyWarpManager = new PartyWarpManager();
        gameModeWarpManager = new GameModeWarpManager();

        new ConfigUtil().createConfig();

        getProxy().registerChannel("serble:serble");
        getProxy().getPluginManager().registerListener(this, new GetConfig());
        getProxy().getPluginManager().registerListener(this, partyManager);
        getProxy().getPluginManager().registerListener(this, new KickListener());
        getProxy().getPluginManager().registerListener(this, new GameModeResumeHandler());

        getProxy().registerChannel("serble:proxyexecute");
        getProxy().getPluginManager().registerListener(this, new ProxyExecuteListener());

        getProxy().registerChannel("serble:play");
        getProxy().registerChannel("calcilator:svtp");
        getProxy().getPluginManager().registerListener(this, gameModeWarpManager);

        getProxy().registerChannel("serble:party");
        getProxy().getPluginManager().registerListener(this, partyWarpManager);

        getProxy().registerChannel("serble:serverping");
        getProxy().getPluginManager().registerListener(this, new ServerPing());

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new SerbleReloadCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PartyCommand("party"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PartyCommand("p"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PlayCommand("play"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new HubCommand("hub"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new HubCommand("lobby"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new HubCommand("l"));

        GetConfig();
    }

    @Override
    public void onDisable() {
        // Get a player for all connected spigot servers but only one for each
        // server. Then send a message to each server to disconnect all players
        // on that server.
        for (String server : ProxyServer.getInstance().getServers().keySet()) {
            ProxyServer.getInstance().getServerInfo(server).getPlayers().stream().findFirst().ifPresent(player -> partyManager.sendResetNotification(player));
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

        Gson gson = new Gson();
        jsonConfig = gson.fromJson(content, ConfigSave.class);

        enabled = true;
    }

    public static PartyManager getPartyManager() {
        return partyManager;
    }

    public static PartyWarpManager getPartyWarpManager() {
        return partyWarpManager;
    }

    public static GameModeWarpManager getGameModeWarpManager() {
        return gameModeWarpManager;
    }
}
