package net.donnypz.mccore.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DayCycle implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!CMDUtils.hasPermission(sender, false, "mc.daycycle")){
            return true;
        }
        Player p = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("day")) {
            setTime(p, 1000, "<green>Day");
        }
        else if (cmd.getName().equalsIgnoreCase("noon")) {
            setTime(p, 6000, "<gold>Noon");
        }
        else if (cmd.getName().equalsIgnoreCase("night")) {
            setTime(p, 18000, "<dark_aqua>Night");
        }
        else if (cmd.getName().equalsIgnoreCase("midnight")) {
            setTime(p, 18000, "<blue>Midnight");
        }
        return true;
    }

    private void setTime(Player player, long time, String timeDisplay){
        player.getWorld().setTime(time);
        player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>The time has been set to "+timeDisplay));
    }
}
