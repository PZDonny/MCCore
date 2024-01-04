package MCCore.commands.minigameCommands;

import MCCore.Core;
import MCCore.sockets.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreatePrivateQueueCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)){
            sender.sendMessage(new TextComponent(ChatColor.RED+"You can only do this command in-game!"));
            return true;
        }
        if (!sender.hasPermission("mc.privatequeue")){
            sender.sendMessage(new TextComponent(ChatColor.RED+"You must be "+ChatColor.LIGHT_PURPLE+"Ender"+ChatColor.RED+" rank or higher to create a private queue!"));
            return true;
        }
        if (args.length < 2){
            sender.sendMessage(new TextComponent(ChatColor.RED+"Incorrect Usage! /pq <minigame> <mode> "));
            return true;
        }
        String privateSettings = "";
        if (args.length == 3){
            privateSettings = args[2];
        }
        p.sendMessage(ChatColor.GRAY+"Attempting to create a private queue...");
        p.playSound(p, Sound.ENTITY_ALLAY_ITEM_THROWN, 1, 1.5f);
        Core.getClient().sendMessage(new String[]{Messages.MINIGAMEAPI_CREATEPRIVATE.getID(), args[0], args[1], p.getUniqueId().toString(), privateSettings});
        return true;
    }
}
