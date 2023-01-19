package MCCore.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Ping implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player t;
        if (args.length > 0) {
            t = Bukkit.getServer().getPlayerExact(args[0]);
            if (t == null) {
                sender.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.RED + " is not online!");
                return false;
            }
        }
        else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Incorrect Usage! /ping <player>");
                return false;
            }
            else {
                t = (Player) sender;
            }
        }
        sender.sendMessage(ChatColor.AQUA + t.getName() + "'s " + ChatColor.WHITE + "ping: " + ChatColor.YELLOW + t.getPing());
        return true;
    }
}
