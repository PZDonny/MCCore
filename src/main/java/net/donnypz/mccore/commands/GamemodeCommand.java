package net.donnypz.mccore.commands;

import net.donnypz.mccore.minigame.arenaManager.ArenaManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)){
            sender.sendMessage(ChatColor.RED+"You can only execute this command in-game!");
            return true;
        }
        if (!p.hasPermission("mc.gamemode")) { //Player w/o perms
            p.sendMessage(ChatColor.RED + "You do not have permission to do this command");
            return true;
        }
        if (ArenaManager.getArenaOfPlayer(p) != null && !p.hasPermission("mc.gamemode.admin")){
            sender.sendMessage(org.bukkit.ChatColor.RED+"You cannot execute this command while in an arena. You must be admin or higher!");
            return true;
        }



        boolean self = true;


        if (args.length > 0) {
            p = Bukkit.getServer().getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.RED + " is not online!");
                return true;
            }
            else if (!sender.hasPermission("mc.gamemode.admin")){
                sender.sendMessage(ChatColor.RED+"You do not have permission to change another player's gamemode!");
                return true;
            }
            self = false;

        }
        String mode = "";
        if (cmd.getName().equalsIgnoreCase("gmc")) {
            p.setGameMode(GameMode.CREATIVE);
            mode = "Creative";
        }

        if (cmd.getName().equalsIgnoreCase("gms")) {
            p.setGameMode(GameMode.SURVIVAL);
            mode = "Survival";
        }

        if (cmd.getName().equalsIgnoreCase("gma")) {
            p.setGameMode(GameMode.ADVENTURE);
            mode = "Adventure";
        }

        if (cmd.getName().equalsIgnoreCase("gmsp")) {
            p.setGameMode(GameMode.SPECTATOR);
            mode = "Spectator";
        }

        p.sendMessage(ChatColor.GREEN + "Your gamemode has been changed to " + ChatColor.AQUA + mode);

        if (!self) {
            sender.sendMessage(ChatColor.YELLOW + "You changed " + ChatColor.WHITE + p.getName() + ChatColor.YELLOW + "'s gamemode to " + ChatColor.AQUA + mode);
        }

        return true;
    }
}
