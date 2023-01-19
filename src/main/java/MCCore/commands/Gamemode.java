package MCCore.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Gamemode implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("mineclassic.gamemode")) { //Player w/o perms
            sender.sendMessage(ChatColor.RED + "You do not have permission to do this command");
            return false;
        }

        Player t = null;

        boolean self = true;
        String mode = "";


        if (!(sender instanceof Player)) { //Console w/ no args
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "You can only set the gamemode of players in console!");
                return false;
            }
        } else {
            t = (Player) sender;
        }
        if (args.length > 0) {
            t = Bukkit.getServer().getPlayerExact(args[0]);
            if (t == null) {
                sender.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.RED + " is not online!");
                return false;
            } else {
                self = false;
            }

        }
        if (cmd.getName().equalsIgnoreCase("gmc")) {
            t.setGameMode(GameMode.CREATIVE);
            mode = "Creative";
        }

        if (cmd.getName().equalsIgnoreCase("gms")) {
            t.setGameMode(GameMode.SURVIVAL);
            mode = "Survival";
        }

        if (cmd.getName().equalsIgnoreCase("gma")) {
            t.setGameMode(GameMode.ADVENTURE);
            mode = "Adventure";
        }

        if (cmd.getName().equalsIgnoreCase("gmsp")) {
            t.setGameMode(GameMode.SPECTATOR);
            mode = "Spectator";
        }

        t.sendMessage(ChatColor.GREEN + "Your gamemode has been changed to " + ChatColor.AQUA + mode);
        if (!self) {
            sender.sendMessage(ChatColor.YELLOW + "You changed " + ChatColor.WHITE + t.getName() + ChatColor.YELLOW + "'s gamemode to " + ChatColor.AQUA + mode);
        }


        return true;
    }
}
