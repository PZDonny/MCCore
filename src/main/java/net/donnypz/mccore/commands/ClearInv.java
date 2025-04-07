package net.donnypz.mccore.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearInv implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender.hasPermission("mc.clearinv"))) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to do this command");
            return false;
        }

        Player p;
        boolean self = true;

        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "You cannot do this command in the console!");
                return false;
            }

        }



        if (args.length > 0) {
            /*if (!(sender.hasPermission("mc.clearinv.others"))){
                sender.sendMessage(ChatColor.RED + "You cannot clear the inventories of other players!");
                return false;
            }*/
            p = Bukkit.getServer().getPlayerExact(args[0]);
            if (p == null) {
                sender.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.RED + " is not online!");
                return false;
            }
            self = false;
        }
        else {
            p = (Player) sender;
        }

        p.getInventory().clear();
        p.sendMessage(ChatColor.YELLOW + "Your inventory has been cleared");
        if (!self) {
            sender.sendMessage(ChatColor.GREEN + "You cleared " + ChatColor.YELLOW + p.getName() + ChatColor.GREEN + "'s inventory");
        }
        return true;
    }
}
