package net.serble.serblebungeeplugin.Schemas.Config;

import java.util.ArrayList;

public class MenuItem {
    public String Name;
    public String Material;
    public Integer Slot;
    public ArrayList<String> Commands;

    public MenuItem() {
        Commands = new ArrayList<>();
    }
}
