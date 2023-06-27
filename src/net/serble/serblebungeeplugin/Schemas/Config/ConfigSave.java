package net.serble.serblebungeeplugin.Schemas.Config;

import java.util.ArrayList;

public class ConfigSave {
    public String ChatFormat;

    public ArrayList<Rank> Ranks;

    public ArrayList<MenuItem> MenuItems;

    public ArrayList<GameModeMenuItem> GameModeMenuItems;

    public ArrayList<StoreItem> StoreItems;

    public ArrayList<GameMode> GameModes;

    public ConfigSave() {
        Ranks = new ArrayList<>();
        MenuItems = new ArrayList<>();
        GameModeMenuItems = new ArrayList<>();
        StoreItems = new ArrayList<>();
        GameModes = new ArrayList<>();
    }

}
