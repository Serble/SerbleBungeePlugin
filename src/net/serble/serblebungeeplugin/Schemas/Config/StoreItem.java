package net.serble.serblebungeeplugin.Schemas.Config;

import java.util.ArrayList;

public class StoreItem {
    public String Name;
    public String Material;
    public Integer Slot;
    public ArrayList<String> Commands;
    public Integer Cost;
    public String Permission;

    public StoreItem() {
        Commands = new ArrayList<>();
    }
}
