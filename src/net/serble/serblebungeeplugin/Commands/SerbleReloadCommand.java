package net.serble.serblebungeeplugin.Commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.serble.serblebungeeplugin.Main;
import net.serble.serblebungeeplugin.Utils;

public class SerbleReloadCommand extends Command {

    public SerbleReloadCommand() {
        super("serblereload");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!commandSender.hasPermission("serble.admin")) {
            commandSender.sendMessage(Utils.getMessage("&cYou do not have permission!"));
            return;
        }

        Main.GetConfig();
        commandSender.sendMessage(Utils.getMessage("&aReloaded config.json!"));
    }
}
