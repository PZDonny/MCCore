package net.donnypz.mccore.command;

import net.donnypz.mccore.database.MongoUtils;
import net.donnypz.mccore.version.VersionHandlerRegistry;
import net.donnypz.mccore.version.CoreAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {
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
            if (CoreAPI.getConfigOptions().connectToMongo){
                sender.sendMessage(Component.text("Attempting to reconnect to MongoDB Database", NamedTextColor.YELLOW));
                MongoUtils.createConnection(CoreAPI.getConfigOptions().connectionString);
            }
            else{
                sender.sendMessage(Component.text("MongoDB has not been enabled in the config! Enable it then run \"/corereload config\"", NamedTextColor.RED));
            }
        }
        else if (arg.equals("config")){
            CoreAPI.getPlugin().reloadConfig();
            VersionHandlerRegistry.updateConfig();
            sender.sendMessage(Component.text("MCCore Config successfully reloaded!", NamedTextColor.GREEN));
            if (!CoreAPI.getConfigOptions().connectToMongo){
                MongoUtils.disconnect();
            }
        }
        else{
            sender.sendMessage(Component.text("Incorrect Usage! /corereload <mongo | config>", NamedTextColor.RED));
        }
        return true;
    }
}
