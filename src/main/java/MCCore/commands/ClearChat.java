package MCCore.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChat implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You cannot do this command in the console!");
            return false;
        }
        for (int i = 0; i < 100; i++) {
            sender.sendMessage(ChatColor.GRAY + " ");
        }
        sender.sendMessage(ChatColor.GREEN + "Your chat has been cleared!");
        return true;
    }
}
