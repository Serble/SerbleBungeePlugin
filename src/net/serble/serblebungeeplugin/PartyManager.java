package net.serble.serblebungeeplugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.serble.serblebungeeplugin.Schemas.Party;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PartyManager implements Listener {
    private final List<Party> parties = new ArrayList<>();

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        Party party = this.getPartyWhereUserIsMember(event.getPlayer());
        if (party == null) {
            return;
        }
        if (party.getLeader() == event.getPlayer().getUniqueId()) {
            this.deleteParty(event.getPlayer());
            partyBroadcast(party, "&cThe party has been disbanded because the leader left.", true);
            return;
        }
        party.removeMember(event.getPlayer().getUniqueId());
        partyBroadcast(party, "&c" + event.getPlayer().getName() + " has left the party because they disconnected.", false, event.getPlayer());
    }

    @EventHandler
    public void onServerChange(ServerSwitchEvent event) {
        Party party = this.getPartyWhereUserIsMember(event.getPlayer());
        if (party == null) {
            return;
        }

        // Send party info to new server
        sendPartyInfoToServer(party, event.getPlayer());
    }

    public void sendResetNotification(ProxiedPlayer player) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("reset");
        player.getServer().getInfo().sendData("serble:party", out.toByteArray());
    }

    public void sendPartyInfoToServer(Party party, ProxiedPlayer player) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("update");
        out.writeUTF(party.serialize());
        player.getServer().getInfo().sendData("serble:party", out.toByteArray());
    }

    public void sendPartyDeleteToServer(Party party, ProxiedPlayer player) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("delete");
        out.writeUTF(party.getLeader().toString());
        player.getServer().getInfo().sendData("serble:party", out.toByteArray());
    }

    public void sendPartyInfoToAllMembersServer(Party party) {
        // Go through each member and send info to server, only send the info to each server once
        List<String> sentTo = new ArrayList<>();
        for (UUID memberId : party.getMembers()) {
            ProxiedPlayer member = ProxyServer.getInstance().getPlayer(memberId);
            if (member == null) continue;
            if (sentTo.contains(member.getServer().getInfo().getName())) continue;
            sentTo.add(member.getServer().getInfo().getName());
            sendPartyInfoToServer(party, member);
        }
    }

    public void sendPartyDeleteToAllMembersServer(Party party) {
        // Go through each member and send info to server, only send the info to each server once
        List<String> sentTo = new ArrayList<>();
        for (UUID memberId : party.getMembers()) {
            ProxiedPlayer member = ProxyServer.getInstance().getPlayer(memberId);
            if (member == null) continue;
            if (sentTo.contains(member.getServer().getInfo().getName())) continue;
            sentTo.add(member.getServer().getInfo().getName());
            sendPartyDeleteToServer(party, member);
        }
    }

    public void partyBroadcast(Party p, String message, boolean ignoreLeader, ProxiedPlayer... ignores) {
        for (UUID memberId : p.getMembers()) {
            if (Arrays.stream(ignores).anyMatch(ign -> ign.getUniqueId() == memberId)) continue;
            ProxiedPlayer member = ProxyServer.getInstance().getPlayer(memberId);
            if (member == null) continue;
            member.sendMessage(Utils.getMessage(message));
        }
        if (ignoreLeader) return;
        ProxiedPlayer leader = ProxyServer.getInstance().getPlayer(p.getLeader());
        leader.sendMessage(Utils.getMessage(message));
    }

    public void createParty(ProxiedPlayer leader) {
        Party party = new Party(leader.getUniqueId());
        this.parties.add(party);
    }

    public void deleteParty(ProxiedPlayer leader) {
        Party party = getParty(leader);
        boolean didDelete = this.parties.removeIf(p -> p.getLeader() == leader.getUniqueId());
        if (didDelete) sendPartyDeleteToServer(party, leader);
    }

    public Party getParty(ProxiedPlayer player) {
        for (Party party : this.parties) {
            if (party.getLeader() == player.getUniqueId()) {
                return party;
            }
        }
        return null;
    }

    public Party getPartyWhereUserIsMember(ProxiedPlayer player) {
        for (Party party : this.parties) {
            if (party.isMember(player.getUniqueId())) {
                return party;
            }
            if (party.getLeader() == player.getUniqueId()) {
                return party;
            }
        }
        return null;
    }

    public boolean ownsParty(ProxiedPlayer player) {
        return this.getParty(player) != null;
    }

    public boolean isMember(ProxiedPlayer player) {
        for (Party party : this.parties) {
            if (party.isMember(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public void addMember(ProxiedPlayer leader, ProxiedPlayer member) {
        for (Party party : this.parties) {
            if (party.getLeader() == leader.getUniqueId()) {
                party.addMember(member.getUniqueId());
            }
        }
    }

    public void removeMember(ProxiedPlayer leader, ProxiedPlayer member) {
        for (Party party : this.parties) {
            if (party.getLeader() == leader.getUniqueId()) {
                party.removeMember(member.getUniqueId());
            }
        }
    }

    public void createInvite(ProxiedPlayer leader, ProxiedPlayer invited) {
        for (Party party : this.parties) {
            if (party.getLeader() == leader.getUniqueId()) {
                party.addInvited(invited.getUniqueId());
            }
        }
    }

    public void removeInvite(ProxiedPlayer leader, ProxiedPlayer invited) {
        for (Party party : this.parties) {
            if (party.getLeader() == leader.getUniqueId()) {
                party.removeInvited(invited.getUniqueId());
            }
        }
    }

    public boolean isInvited(ProxiedPlayer leader, ProxiedPlayer invited) {
        for (Party party : this.parties) {
            if (party.getLeader() == leader.getUniqueId()) {
                return party.getInvited().contains(invited.getUniqueId());
            }
        }
        return false;
    }

}
