package net.serble.serblebungeeplugin.Commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.serble.serblebungeeplugin.Main;
import net.serble.serblebungeeplugin.Schemas.Party;
import net.serble.serblebungeeplugin.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyCommand extends Command implements TabExecutor {

    public PartyCommand(String alias) {
        super(alias);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Utils.getMessage("&cYou must be a player to use this command!"));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 0) {
            sendUsage(player);
            return;
        }

        switch (args[0]) {
            case "accept": {
                if (args.length != 2) {
                    sendUsage(player);
                    return;
                }

                Party party = Main.getPartyManager().getPartyWhereUserIsMember(player);
                if (party != null) {
                    player.sendMessage(Utils.getMessage("&cYou are already in a party!"));
                    return;
                }

                ProxiedPlayer leader = ProxyServer.getInstance().getPlayer(args[1]);
                if (leader == null) {
                    player.sendMessage(Utils.getMessage("&cThat player is not online!"));
                    return;
                }

                party = Main.getPartyManager().getParty(leader);
                if (party == null) {
                    player.sendMessage(Utils.getMessage("&cThat player does not have a party!"));
                    return;
                }

                if (!Main.getPartyManager().isInvited(leader, player) && !party.isOpen()) {
                    player.sendMessage(Utils.getMessage("&cYou are not invited to that party!"));
                    return;
                }

                Main.getPartyManager().addMember(leader, player);
                Main.getPartyManager().removeInvite(leader, player);
                sendJoinMessage(player, party);
                Main.getPartyManager().sendPartyInfoToAllMembersServer(party);
                break;
            }

            case "kick": {
                if (args.length != 2) {
                    sendUsage(player);
                    return;
                }

                Party party = Main.getPartyManager().getPartyWhereUserIsMember(player);
                if (party == null) {
                    player.sendMessage(Utils.getMessage("&cYou are not in a party!"));
                    return;
                }

                if (!party.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage(Utils.getMessage("&cYou are not the leader of this party!"));
                    return;
                }

                ProxiedPlayer member = ProxyServer.getInstance().getPlayer(args[1]);
                if (member == null) {
                    player.sendMessage(Utils.getMessage("&cThat player is not online!"));
                    return;
                }

                if (!party.getMembers().contains(member.getUniqueId())) {
                    player.sendMessage(Utils.getMessage("&cThat player is not in your party!"));
                    return;
                }

                Main.getPartyManager().removeMember(player, member);
                player.sendMessage(Utils.getMessage("&aYou have kicked " + member.getName() + " from the party!"));
                member.sendMessage(Utils.getMessage("&cYou have been kicked from the party!"));
                Main.getPartyManager().partyBroadcast(party, "&7" + player.getName() + " &ahas been kicked from the party.", true, member);
                Main.getPartyManager().sendPartyInfoToAllMembersServer(party, member);
                break;
            }

            case "leave": {
                if (args.length != 1) {
                    sendUsage(player);
                    return;
                }

                Party party = Main.getPartyManager().getPartyWhereUserIsMember(player);
                if (party == null) {
                    player.sendMessage(Utils.getMessage("&cYou are not in a party!"));
                    return;
                }

                if (party.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage(Utils.getMessage("&cYou are the leader of this party! Do &7/p disband&c to disband the party!"));
                    return;
                }

                Main.getPartyManager().removeMember(ProxyServer.getInstance().getPlayer(party.getLeader()), player);
                player.sendMessage(Utils.getMessage("&aYou have left the party!"));
                Main.getPartyManager().partyBroadcast(party, "&7" + player.getName() + " &ahas left the party.", false, player);
                Main.getPartyManager().sendPartyInfoToAllMembersServer(party, player);
                break;
            }

            case "disband": {
                if (args.length != 1) {
                    sendUsage(player);
                    return;
                }

                Party party = Main.getPartyManager().getPartyWhereUserIsMember(player);
                if (party == null) {
                    player.sendMessage(Utils.getMessage("&cYou are not in a party!"));
                    return;
                }

                if (!party.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage(Utils.getMessage("&cYou are not the leader of this party!"));
                    return;
                }

                Main.getPartyManager().deleteParty(player);
                Main.getPartyManager().partyBroadcast(party, "&cThe party has been disbanded.", true);
                player.sendMessage(Utils.getMessage("&aYou have disbanded the party!"));
                break;
            }

            case "warp": {
                if (args.length != 1) {
                    sendUsage(player);
                    return;
                }

                Party party = Main.getPartyManager().getPartyWhereUserIsMember(player);
                if (party == null) {
                    player.sendMessage(Utils.getMessage("&cYou are not in a party!"));
                    return;
                }

                if (!party.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage(Utils.getMessage("&cYou are not the leader of this party!"));
                    return;
                }

                // Send all players to the leader's server
                Main.getPartyWarpManager().warp(party, true, false);
                player.sendMessage(Utils.getMessage("&aYou have warped your party to your game mode!"));
                break;
            }

            case "list": {
                if (args.length != 1) {
                    sendUsage(player);
                    return;
                }

                Party party = Main.getPartyManager().getPartyWhereUserIsMember(player);
                if (party == null) {
                    player.sendMessage(Utils.getMessage("&cYou are not in a party!"));
                    return;
                }

                player.sendMessage(Utils.getMessage("&aParty members:"));

                // Combine all the names into one string (etc. "Serble, Notch, Dinnerbone")
                StringBuilder names = new StringBuilder();
                for (UUID uuid : party.getMembers()) {
                    ProxiedPlayer member = ProxyServer.getInstance().getPlayer(uuid);
                    if (member == null) {
                        continue;
                    }
                    names.append(member.getName()).append(", ");
                }

                // Remove the last comma and space
                if (names.length() > 2) names.setLength(names.length() - 2); else names.append("None");

                player.sendMessage(Utils.getMessage("&aLeader: &7" + ProxyServer.getInstance().getPlayer(party.getLeader()).getName()));
                player.sendMessage(Utils.getMessage("&aMembers: &7" + names));
                break;
            }

            case "invite":
            default: {
                // Invite a player
                if (args.length != 2 && args.length != 1) {
                    sendUsage(player);
                    return;
                }

                int playerArgIndex = 1;
                if (args.length == 1) {
                    playerArgIndex = 0;
                }

                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[playerArgIndex]);
                if (target == null) {
                    player.sendMessage(Utils.getMessage("&cThat player is not online!"));
                    return;
                }

                if (target == player) {
                    player.sendMessage(Utils.getMessage("&cYou cannot invite yourself!"));
                    return;
                }

                Party party = Main.getPartyManager().getPartyWhereUserIsMember(player);
                if (party != null) {
                    if (party.getLeader() != player.getUniqueId()) {
                        player.sendMessage(Utils.getMessage("&cOnly the leader can invite users"));
                        return;
                    }
                } else {
                    Main.getPartyManager().createParty(player);
                    Main.getPartyManager().sendPartyInfoToAllMembersServer(Main.getPartyManager().getParty(player));
                }

                if (Main.getPartyManager().isInvited(player, target)) {
                    player.sendMessage(Utils.getMessage("&cYou have already invited that player!"));
                    return;
                }

                sendInviteMessage(player, target);
                Main.getPartyManager().createInvite(player, target);
            }  // Default
        }  // Switch
    }  // Execute

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Utils.getMessage("&b------------------------------------"));
        sender.sendMessage(Utils.getMessage("&a/party <username> &7- Invite a player to your party."));
        sender.sendMessage(Utils.getMessage("&a/party invite <username> &7- Invite a player to your party."));
        sender.sendMessage(Utils.getMessage("&a/party accept <username> &7- Accept a party invite."));
        sender.sendMessage(Utils.getMessage("&a/party kick <username> &7- Kick a player from your party."));
        sender.sendMessage(Utils.getMessage("&a/party leave &7- Leave your current party."));
        sender.sendMessage(Utils.getMessage("&a/party disband &7- Disband your current party."));
        sender.sendMessage(Utils.getMessage("&a/party warp &7- Teleport all players to the game you are playing."));
        sender.sendMessage(Utils.getMessage("&a/party list &7- List all players in your party."));
        sender.sendMessage(Utils.getMessage("&b------------------------------------"));
    }

    private void sendInviteMessage(ProxiedPlayer sender, ProxiedPlayer target) {
        sender.sendMessage(Utils.getMessage("&aYou have invited &b" + target.getName() + " &ato your party!"));
        target.sendMessage(Utils.getMessage("&b" + sender.getName() + " &ahas invited you to their party!"));

        TextComponent component = new TextComponent();
        component.setText(Utils.colours("&6Click here to accept!"));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.colours("&a/party accept " + sender.getName()))));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + sender.getName()));

        target.sendMessage(component);
    }

    private void sendJoinMessage(ProxiedPlayer newUser, Party target) {
        newUser.sendMessage(Utils.getMessage("&aYou have joined &b" + ProxyServer.getInstance().getPlayer(target.getLeader()).getName() + "'s &aparty!"));
        Main.getPartyManager().partyBroadcast(target, "&b" + newUser.getName() + " &ahas joined the party!", false, newUser);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return null;
        ProxiedPlayer player = (ProxiedPlayer) sender;

        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            results.add("invite");
            results.add("accept");
            results.add("kick");
            results.add("leave");
            results.add("disband");
            results.add("warp");
            results.add("list");
        } else if (args.length == 2) {
            Party party = Main.getPartyManager().getPartyWhereUserIsMember(player);
            boolean isLeader = party != null && party.getLeader() == player.getUniqueId();

            switch (args[0]) {
                case "kick":
                    if (party == null || !isLeader) break;
                    for (UUID playerId : party.getMembers()) {
                        ProxiedPlayer member = ProxyServer.getInstance().getPlayer(playerId);
                        if (member == null) continue;
                        results.add(member.getName());
                    }
                    break;

                case "invite":
                    if (party != null && !isLeader) break;
                    for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                        if (p == player) continue;
                        results.add(p.getName());
                    }
                    break;

                case "accept":
                    for (Party partyIWasInvitedTo : Main.getPartyManager().getPartiesUserInInvitedTo(player)) {
                        results.add(ProxyServer.getInstance().getPlayer(partyIWasInvitedTo.getLeader()).getName());
                    }
                    break;
            }
        }
        return results;
    }

}
