package MCCore.commands;

import MCCore.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class PreviewBorder implements CommandExecutor {

    private final HashSet<Player> previewers = new HashSet<>();
    private final String incorrectUsage = ChatColor.RED+"Incorrect Usage! /previewborder <size-in-blocks> <duration-in-seconds>";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mc.previewborder")) {
            sender.sendMessage(ChatColor.RED+ "You do not have permission to do this command");
            return false;
        }

        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED+"You can only run this command in-game!");
            return true;
        }
        Player p = (Player) sender;
        if (args.length == 0){
            p.sendMessage(incorrectUsage);
            return true;
        }


        try{
            int duration = 5;
            if (args.length >= 2){
                duration = Integer.parseInt(args[1]);
                if (duration <= 0){
                    p.sendMessage(ChatColor.RED+"The duration cannot be shorter than 1 second!");
                    return true;
                }
            }
            previewBorder(p, Double.parseDouble(args[0]), duration);
        }
        catch (NumberFormatException ex){
            p.sendMessage(incorrectUsage);
        }

        return true;
    }

    private void previewBorder(Player p, double size, int duration){
        if (previewers.contains(p)){
            p.sendMessage(ChatColor.RED+"You are already viewing a world border, please wait!");
        }
        if (size < 1){
            p.sendMessage(ChatColor.RED+"World border size cannot be smaller than 1!");
        }
        p.sendMessage(ChatColor.YELLOW+"Displaying world border for 5 seconds");
        WorldBorder border = Bukkit.createWorldBorder();
        border.setCenter(p.getLocation());
        border.setSize(size);
        p.setWorldBorder(border);
        new BukkitRunnable(){
            public void run(){
                if (p.isOnline()) p.setWorldBorder(null);
                previewers.remove(p);
            }
        }.runTaskLater(Core.getInstance(), 20L*duration);
    }
}
