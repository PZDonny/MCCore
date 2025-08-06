package net.donnypz.mccore.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class CMDUtils {

    static boolean hasPermission(@NotNull CommandSender sender, boolean consoleAllowed, @Nullable String permission){
        if (!consoleAllowed){
            if (!(sender instanceof Player)){
                sender.sendMessage(Component.text("You cannot do this command in the console!", NamedTextColor.RED));
                return false;
            }
        }

        if (permission != null){
            if (!(sender.hasPermission(permission))) {
                sender.sendMessage(Component.text("You do not have permission to do this command", NamedTextColor.RED));
                return false;
            }
        }
        return true;
    }
}
