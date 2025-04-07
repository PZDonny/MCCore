package net.donnypz.mccore.commands;

import net.donnypz.mccore.minigame.arenaManager.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Fly implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("mc.fly")){
            sender.sendMessage(ChatColor.RED + "You do not have permission to do this command");
            return true;
        }
        if (!(sender instanceof Player p)) { //Console w/ no args
            sender.sendMessage(ChatColor.RED + "You can only execute this command in-game");
            return true;
        }

        if (ArenaManager.getArenaOfPlayer(p) != null && !p.hasPermission("mc.fly.admin")){
            sender.sendMessage(ChatColor.RED+"You cannot execute this command here!");
            return true;
        }

        if (args.length > 0) { //Setting another player's flight
            p = Bukkit.getServer().getPlayerExact(args[0]);
            if (p == null) {
                sender.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.RED + " is not online!");
                return true;
            }

            if (sender.hasPermission("mc.fly.admin")) {
                doFlight(p, sender);
            }
            else{
                sender.sendMessage(ChatColor.RED+"You cannot set the flight of other players. You must be admin or higher!");
            }
        }
        else {
            doFlight(p, null);
        }
        return true;
    }


    public void doFlight(Player t, CommandSender sender) {
        if (!(t.getAllowFlight())) {
            t.setAllowFlight(true);
            t.sendMessage(ChatColor.AQUA+"You can fly!");
            if (sender != null) {
                sender.sendMessage(ChatColor.GREEN + "You enabled flight for " + ChatColor.YELLOW + t.getName());
            }
        }
        else {
            t.setAllowFlight(false);
            t.setFlying(false);
            t.sendMessage(ChatColor.GRAY+"You can no longer fly!");
            if (sender != null) {
                sender.sendMessage(ChatColor.RED + "You disabled flight for " + ChatColor.YELLOW + t.getName());
            }
        }
    }
}
