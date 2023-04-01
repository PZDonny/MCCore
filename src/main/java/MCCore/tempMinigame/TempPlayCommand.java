package MCCore.tempMinigame;

import MCCore.minigameAPI.MinigameJoin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TempPlayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)){
            return true;
        }
        if (args.length == 0){
            return false;
        }
        else{
            Player p = (Player) sender;
            MinigameJoin.attempt(p, args[0], "normal");
        }
        return true;
    }
}
