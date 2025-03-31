package it.pose.autoreplant;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {

    private final AutoReplant plugin;

    public Commands(AutoReplant plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("autoreplant")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("autoreplant.reload")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission!");
                    return true;
                }

                plugin.reload();
                sender.sendMessage(ChatColor.GREEN + "AutoReplant config reloaded!");
                return true;
            }
        }
        return false;
    }
}
