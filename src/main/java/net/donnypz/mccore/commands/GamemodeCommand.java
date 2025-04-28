package net.donnypz.mccore.commands;

import net.donnypz.mccore.minigame.arena.ArenaManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!CMDUtils.validate(sender, true, "mc.gamemode")) {
            return true;
        }

        Player p;
        boolean self;
        if (args.length == 0){
            if (!(sender instanceof Player)){
                sender.sendMessage(Component.text("You must specify a player!", NamedTextColor.RED));
                return true;
            }
            else{
                p = (Player) sender;
                self= true;
                if (ArenaManager.getArenaOfPlayer(p) != null && !p.hasPermission("mc.gamemode.admin")){
                    sender.sendMessage(Component.text("You cannot execute this command while in an arena. You must be admin or higher!", NamedTextColor.RED));
                    return true;
                }
            }
        }
        else{
            p = Bukkit.getServer().getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>"+args[0]+" <red>is not online!"));
                return true;
            }
            else if (!sender.hasPermission("mc.gamemode.admin")){
                sender.sendMessage(Component.text("You do not have permission to change another player's gamemode!", NamedTextColor.RED));
                return true;
            }
            self = false;
        }

        String mode = "";
        if (cmd.getName().equalsIgnoreCase("gmc")) {
            p.setGameMode(GameMode.CREATIVE);
            mode = "Creative";
        }

        if (cmd.getName().equalsIgnoreCase("gms")) {
            p.setGameMode(GameMode.SURVIVAL);
            mode = "Survival";
        }

        if (cmd.getName().equalsIgnoreCase("gma")) {
            p.setGameMode(GameMode.ADVENTURE);
            mode = "Adventure";
        }

        if (cmd.getName().equalsIgnoreCase("gmsp")) {
            p.setGameMode(GameMode.SPECTATOR);
            mode = "Spectator";
        }

        p.sendMessage(MiniMessage.miniMessage().deserialize("<green>Your gamemode has been changed to <aqua>"+mode));

        if (!self) {
            sender.sendMessage(ChatColor.YELLOW + "You changed " + ChatColor.WHITE + p.getName() + ChatColor.YELLOW + "'s gamemode to " + ChatColor.AQUA + mode);
        }

        return true;
    }
}
