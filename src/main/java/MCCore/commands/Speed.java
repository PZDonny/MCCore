package MCCore.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Speed implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("mc.speed")){
            sender.sendMessage(ChatColor.RED + "You do not have permission to do this command");
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You cannot do this in the console!");
            return false;
        }


        Player p = (Player) sender;
        if (args.length == 0){
            incorrectUsage(p);
            return true;
        }
        if (!args[0].isEmpty()){
            try {
                switch(Integer.parseInt(args[0])) {
                    case 1:
                        p.sendMessage(ChatColor.GREEN+"Your speed has been set to"+ChatColor.GOLD+" 1 "+ChatColor.WHITE+"("+ChatColor.BLUE+"default"+ChatColor.WHITE+")");
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
        p.sendMessage(ChatColor.GREEN+"Your speed has been set to "+ChatColor.GOLD+ arg);
    }

    private void incorrectUsage(Player p) {
        p.sendMessage(ChatColor.RED+"Incorrect Usage! /speed <1-10>");
    }
}
