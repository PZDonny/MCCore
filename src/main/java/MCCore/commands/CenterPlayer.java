package MCCore.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CenterPlayer implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)){
            sender.sendMessage(ChatColor.RED+"You can only execute this command in-game!");
            return true;
        }

        if (!p.hasPermission("minigame.setup")){
            p.sendMessage(ChatColor.RED+"You do not have permission to execute this command!");
            return true;
        }

        Location loc = p.getLocation();
        loc.setX(Math.floor(loc.getX())+0.5);
        loc.setY(Math.floor(loc.getY())+0.5);
        loc.setZ(Math.floor(loc.getZ())+0.5);
        loc.setPitch(0.001f);
        p.teleport(loc);

        p.sendMessage(ChatColor.GREEN+"Centered!");
        return true;
    }
}
