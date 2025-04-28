package net.donnypz.mccore;

import net.donnypz.mccore.database.MongoUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && !(sender.hasPermission("mccore.reload"))) {
            sender.sendMessage(Component.text("You do not have permission to do this command!", NamedTextColor.RED));
            return true;
        }
        if (args.length < 1){
            sender.sendMessage(Component.text("Incorrect Usage! /corereload <mongo  | config>", NamedTextColor.RED));
            return true;
        }
        String arg = args[0];
        if (arg.equals("mongo")){
            if (Core.isMongoEnabled()){
                sender.sendMessage(Component.text("Attempting to reconnect to MongoDB Database", NamedTextColor.YELLOW));
                MongoUtils.createConnection(Core.connectionString);
            }
            else{
                sender.sendMessage(Component.text("MongoDB has not been enabled in the config! Enable it then run \"/corereload config\"", NamedTextColor.RED));
            }
        }
        else if (arg.equals("config")){
            Core.getInstance().reloadConfig();
            ConfigLoader.loadConfig();
            sender.sendMessage(Component.text("MCCore Config successfully reloaded!", NamedTextColor.GREEN));
            if (!Core.isMongoEnabled()){
                MongoUtils.disconnect();
            }
        }
        else{
            sender.sendMessage(Component.text("Incorrect Usage! /corereload <mongo | config>", NamedTextColor.RED));
        }
        return true;
    }
}
