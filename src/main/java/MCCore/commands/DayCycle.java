package MCCore.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DayCycle implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("mc.daycycle")){
            sender.sendMessage(ChatColor.RED + "You do not have permission to do this command");
            return false;
        }
        if (!(sender instanceof Player p)) {
            sender.sendMessage(ChatColor.RED + "You cannot do this in the console!");
            return false;
        }
        if (cmd.getName().equalsIgnoreCase("day")) {
            p.getWorld().setTime(1000);
            p.sendMessage(ChatColor.YELLOW+"The time has been set to "+ChatColor.GREEN+"Day");
        }
        if (cmd.getName().equalsIgnoreCase("noon")) {
            p.getWorld().setTime(6000);
            p.sendMessage(ChatColor.YELLOW+"The time has been set to "+ChatColor.GOLD+"Noon");
        }
        if (cmd.getName().equalsIgnoreCase("night")) {
            p.getWorld().setTime(13000);
            p.sendMessage(ChatColor.YELLOW+"The time has been set to "+ChatColor.DARK_AQUA+"Night");
        }
        if (cmd.getName().equalsIgnoreCase("midnight")) {
            p.getWorld().setTime(18000);
            p.sendMessage(ChatColor.YELLOW+"The time has been set to "+ChatColor.BLUE+"Midnight");
        }

        return true;
    }
}
