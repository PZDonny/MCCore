package net.donnypz.mccore.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ClearChat implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!CMDUtils.hasPermission(sender, false, null)){
            return true;
        }

        Component comp = Component.empty();
        for (int i = 0; i < 100; i++) {
            comp = comp.append(Component.newline());
        }
        sender.sendMessage(comp);
        sender.sendMessage(Component.text("Your chat has been cleared!", NamedTextColor.GREEN));
        return true;
    }
}
