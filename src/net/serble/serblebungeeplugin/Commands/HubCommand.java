package net.serble.serblebungeeplugin.Commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.serble.serblebungeeplugin.ConfigUtil;

public class HubCommand extends Command {

    public HubCommand(String alias) {
        super(alias);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String lobby = ConfigUtil.configuration.getString("hub-gamemode");
        ProxyServer.getInstance().getPluginManager().dispatchCommand(sender, "play " + lobby);
    }

}
