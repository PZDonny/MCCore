package net.donnypz.mccore.command;

import net.donnypz.mccore.minigame.arena.ArenaManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Speed implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!CMDUtils.hasPermission(sender, false, "mc.speed")){
            return true;
        }

        Player p = (Player) sender;
        if (ArenaManager.getArenaOfPlayer(p) != null && !p.hasPermission("mc.admin")){
            sender.sendMessage(Component.text("You cannot execute this command while in an arena. You must be admin or higher!", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0){
            incorrectUsage(p);
            return true;
        }
        if (!args[0].isEmpty()){
            try {
                switch(Integer.parseInt(args[0])) {
                    case 1:
                        p.sendMessage(MiniMessage.miniMessage().deserialize("<green>Your speed has been set to <gold>1 <white>(<blue>default<white>)"));
                        p.setWalkSpeed(0.2f);
                        p.setFlySpeed(0.1f);
                        break;
                    case 2: setSpeed(p, 0.3, args[0]);
                        break;
                    case 3: setSpeed(p, 0.4, args[0]);
                        break;
                    case 4: setSpeed(p, 0.5, args[0]);
                        break;
                    case 5: setSpeed(p, 0.6, args[0]);
                        break;
                    case 6: setSpeed(p, 0.7, args[0]);
                        break;
                    case 7: setSpeed(p, 0.8, args[0]);
                        break;
                    case 8: setSpeed(p, 0.9, args[0]);
                        break;
                    case 9: setSpeed(p, 0.95, args[0]);
                        break;
                    case 10: setSpeed(p, 1, args[0]);
                        break;
                    default:
                        incorrectUsage(p);
                        break;
                }
            }catch(NumberFormatException e) {
                incorrectUsage(p);
            }
        }
        else {
            incorrectUsage(p);
        }
        return true;
    }
    private void setSpeed(Player p, double speed, String arg) {
        p.setWalkSpeed((float) speed);
        p.setFlySpeed((float) (speed-0.1));
        p.sendMessage(MiniMessage.miniMessage().deserialize("<green>Your speed has been set to <gold>"+arg));
    }

    private void incorrectUsage(Player p) {
        p.sendMessage(Component.text("Incorrect Usage! /speed <1-10>", NamedTextColor.RED));
    }
}
