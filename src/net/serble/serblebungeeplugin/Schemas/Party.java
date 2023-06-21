package net.serble.serblebungeeplugin.Schemas;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {
    private final UUID leader;
    private final List<UUID> members;
    private final List<UUID> invited;
    private boolean open;

    public Party(UUID leader) {
        this.leader = leader;
        this.members = new ArrayList<>();
        this.invited = new ArrayList<>();
        this.open = false;
    }

    public void addMember(UUID player) {
        this.members.add(player);
    }

    public void removeMember(UUID player) {
        this.members.removeIf(m -> m.equals(player));
    }

    public boolean isMember(UUID player) {
        return this.members.contains(player);
    }

    public void addInvited(UUID player) {
        this.invited.add(player);
    }

    public void removeInvited(UUID player) {
        this.invited.removeIf(m -> m.equals(player));
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return this.open;
    }

    public UUID getLeader() {
        return this.leader;
    }

    public List<UUID> getMembers() {
        return this.members;
    }

    public List<UUID> getInvited() {
        return this.invited;
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
