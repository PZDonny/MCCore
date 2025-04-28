package net.donnypz.mccore.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChat implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!CMDUtils.validate(sender, false, null)){
            return false;
        }

        for (int i = 0; i < 100; i++) {
            sender.sendMessage(Component.empty());
        }
        sender.sendMessage(Component.text("Your chat has been cleared!", NamedTextColor.GREEN));
        return true;
    }
}
