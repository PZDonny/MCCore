package net.donnypz.mccore.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearInv implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!CMDUtils.validate(sender, true, "mc.clearinv")){
            return false;
        }

        Player p;
        boolean self = true;

        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sender.sendMessage(Component.text("You cannot do this command in the console!", NamedTextColor.RED));
                return false;
            }
        }



        if (args.length > 0) {
            /*if (!(sender.hasPermission("mc.clearinv.others"))){
                sender.sendMessage(ChatColor.RED + "You cannot clear the inventories of other players!");
                return false;
            }*/
            p = Bukkit.getServer().getPlayerExact(args[0]);
            if (p == null) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>"+args[0]+" <red>is not online!"));
                return false;
            }
            self = false;
        }
        else {
            p = (Player) sender;
        }

        p.getInventory().clear();
        p.sendMessage(Component.text("Your inventory has been cleared", NamedTextColor.YELLOW));
        if (!self) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>You cleared <yellow>"+p.getName()+"<green>'s inventory"));
        }
        return true;
    }
}
