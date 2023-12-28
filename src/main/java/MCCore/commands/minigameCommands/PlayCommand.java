package MCCore.commands.minigameCommands;

import MCCore.minigameAPI.PlayMinigame;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)){
            return true;
        }
        if (args.length < 1){
            sender.sendMessage(ChatColor.RED+"Incorrect Usage! /play <minigame> [mode]");
            return false;
        }
        else if (args.length == 1){
            Player p = (Player) sender;
            PlayMinigame.join(p, args[0], "", true);
        }
        else{
            Player p = (Player) sender;
            PlayMinigame.join(p, args[0], args[1], true);
        }
        return true;
    }
}
