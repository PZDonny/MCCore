package net.donnypz.mccore.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            sender.sendMessage(Component.text("You can only execute this command in-game!", NamedTextColor.RED));
            return true;
        }

        if (!p.hasPermission("minigame.setup")){
            p.sendMessage(Component.text("You do not have permission to execute this command!", NamedTextColor.RED));
            return true;
        }

        Location loc = p.getLocation();
        loc.setX(Math.floor(loc.getX())+0.5);
        loc.setY(Math.floor(loc.getY())+0.5);
        loc.setZ(Math.floor(loc.getZ())+0.5);
        loc.setPitch(0.001f);
        if (args.length >= 1){
            try{
                loc.setYaw(Float.parseFloat(args[0]));
            }
            catch(IllegalArgumentException e){
                p.sendMessage(Component.text("Invalid Yaw! Type a valid number.", NamedTextColor.RED));
                return true;
            }
        }

        p.teleport(loc);
        p.sendMessage(Component.text("Centered!", NamedTextColor.GREEN));
        return true;
    }
}
