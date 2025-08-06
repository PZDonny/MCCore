package net.donnypz.mccore.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CenterPlayer implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!CMDUtils.hasPermission(sender, false, "minigame.setup")){
            return true;
        }

        Player p = (Player) sender;

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
