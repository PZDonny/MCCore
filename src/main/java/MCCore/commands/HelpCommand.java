package MCCore.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p){
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 1);
        }
        sender.sendMessage(ChatColor.GRAY+""+ChatColor.BOLD+"---------="+ChatColor.GOLD+"Helpful Commands"+ ChatColor.GRAY+""+ChatColor.BOLD+"=---------");
        sender.sendMessage(ChatColor.GREEN+"/lobby"+ChatColor.GRAY+" (Returns to the lobby)");
        sender.sendMessage(ChatColor.AQUA+"/friends"+ChatColor.GRAY+" (Add players as friends)");
        sender.sendMessage(ChatColor.LIGHT_PURPLE+"/party"+ChatColor.GRAY+" (Party and play games with other players)");
        sender.sendMessage(ChatColor.RED+"/ignore"+ChatColor.GRAY+" (Ignore/Block disruptive players)");
        sender.sendMessage(ChatColor.RED+"/report"+ChatColor.GRAY+" (Report cheaters and disruptive players)");
        sender.sendMessage(ChatColor.BLUE+"/discord"+ChatColor.GRAY+" (Join our discord!)");
        return true;
    }
}
