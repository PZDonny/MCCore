package MCCore.commands;

import MCCore.utils.Disguise.DisguiseHandler;
import dev.iiahmed.disguise.PlayerInfo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;


public class RealName implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mc.realname")){
            sender.sendMessage(ChatColor.RED+"You do not have permission to run this command!");
            return true;
        }
        if (args.length == 0){
            sendIncorrectUsage(sender);
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < args.length; i++){
            builder.append(args[i]);
            if (i+1 != args.length){
                builder.append(" ");
            }
        }
        String nick = builder.toString();
        PlayerInfo info = DisguiseHandler.getPlayerInfo(nick);
        if (info == null){
            playerNotFound(sender);
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW+nick+"'s "+ChatColor.AQUA+"real name is "+ChatColor.WHITE+info.getName());
        return true;
    }

    private void sendIncorrectUsage(CommandSender sender){
        sender.sendMessage(ChatColor.RED+"Incorrect Usage! /realname <nickname>");
    }

    private void playerNotFound(CommandSender sender){
        sender.sendMessage(ChatColor.RED+"No player with the given name could be found!");
    }

}
