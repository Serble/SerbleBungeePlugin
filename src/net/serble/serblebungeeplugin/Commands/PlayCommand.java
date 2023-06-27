package net.serble.serblebungeeplugin.Commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.serble.serblebungeeplugin.Main;
import net.serble.serblebungeeplugin.Schemas.Config.GameMode;
import net.serble.serblebungeeplugin.Schemas.Party;
import net.serble.serblebungeeplugin.Utils;

import java.util.ArrayList;
import java.util.List;

public class PlayCommand extends Command implements TabExecutor {

    public PlayCommand(String alias) {
        super(alias);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Utils.getMessage("&cOnly players can do this"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length != 1) {
            sender.sendMessage(Utils.getMessage("&cUsage: /play <gamemode>"));
            return;
        }

        for (GameMode gm : Main.jsonConfig.GameModes) {
            if (!gm.Name.equalsIgnoreCase(args[0])) continue;
            if (!sender.hasPermission(gm.Permission)) {
                sender.sendMessage(Utils.getMessage("&cYou do not have permission!"));
                return;
            }

            if (gm.TriggersWarp) {
                if (Main.getPartyManager().isMember(player)) {
                    sender.sendMessage(Utils.getMessage("&cOnly the party leader can join games!"));
                    return;
                }
            }

            // Send them
            Main.getGameModeWarpManager().sendPlayer(player, gm.Name);
            player.sendMessage(Utils.getMessage("&aSending you... &7(" + gm.Server + ", " + gm.World + ")"));

            if (!gm.TriggersWarp) return;

            Party party = Main.getPartyManager().getPartyWhereUserIsMember(player);
            if (party == null) return;
            Main.getPartyWarpManager().warp(party, false, true);
            return;
        }

        sender.sendMessage(Utils.getMessage("&cThat GameMode doesn't exist!"));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length != 1) return null;
        List<String> results = new ArrayList<>();
        for (GameMode gm : Main.jsonConfig.GameModes) {
            if (gm.Name.toLowerCase().startsWith(args[0].toLowerCase())) {
                results.add(gm.Name);
            }
        }
        return results;
    }
}
