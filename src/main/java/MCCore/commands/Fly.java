package MCCore.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Fly implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("mineclassic.fly.use")){
            sender.sendMessage(ChatColor.RED + "You do not have permission to do this command");
            return false;
        }

        Player t;

        if (args.length > 0) { //Setting another player's flight
            t = Bukkit.getServer().getPlayerExact(args[0]);
            if (t == null) {
                sender.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.RED + " is not online!");
                return false;
            }

            if (sender.hasPermission("mineclassic.fly.s")) {
                doFlight(t, sender);
            }

        }
        else {
            if (!(sender instanceof Player)) { //Console w/ no args
                sender.sendMessage(ChatColor.RED + "You can only set the flight of players in console!");
                return false;
            }

            t = (Player) sender;
            if (t.getWorld().toString().contains("lobby")) {
                doFlight(t, null);
            }
            else {
                if (!(t.hasPermission("mineclassic.fly.s"))){
                    sender.sendMessage(ChatColor.RED + "You cannot fly here!");
                    return false;
                }
                doFlight(t, null);
            }
        }
        return true;
    }


    public void doFlight(Player t, CommandSender sender) {
        if (!(t.getAllowFlight())) {
            t.setAllowFlight(true);
            t.sendMessage("You can fly!");
            if (sender != null) {
                sender.sendMessage(ChatColor.GREEN + "You enabled flight for " + ChatColor.YELLOW + t.getName());
            }
        }
        else {
            t.setAllowFlight(false);
            t.setFlying(false);
            t.sendMessage("You can no longer fly!");
            if (sender != null) {
                sender.sendMessage(ChatColor.RED + "You disabled flight for " + ChatColor.YELLOW + t.getName());
            }
        }
    }
}
