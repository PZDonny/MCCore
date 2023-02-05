package MCCore;

import MCCore.commands.*;
import MCCore.events.ProjectileRandomness;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.UUID;

public final class Core extends JavaPlugin {
    private FileConfiguration config = getConfig();
    public boolean projectileRandomness = getConfig().getBoolean("fixProjectileRandomness");
    public boolean connectToMongo = getConfig().getBoolean("connectToMongo");

    private final String serverPrefix = ChatColor.GREEN+"Mine"+ChatColor.WHITE+"Classic";
    public static WorldGuardPlugin WGAPI;
    @Override
    public void onEnable() {
        config.options().copyDefaults(true);
        saveConfig();
        //WGAPI = getWorldGuard();
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "M" + ChatColor.WHITE + "C" + ChatColor.DARK_RED + "CORE " + ChatColor.GREEN + "ENABLED");
        new BukkitRunnable() {//Connect to Mongo Asynchronously
            public void run() {
                if (connectToMongo) connectDB.connectToMongo();
            }
        }.runTaskAsynchronously(this);

        //Commands
        getCommand("gmc").setExecutor(new Gamemode());
        getCommand("gms").setExecutor(new Gamemode());
        getCommand("gma").setExecutor(new Gamemode());
        getCommand("gmsp").setExecutor(new Gamemode());
        getCommand("fly").setExecutor(new Fly());
        getCommand("speed").setExecutor(new Speed());
        getCommand("cc").setExecutor(new ClearChat());
        getCommand("ci").setExecutor(new ClearInv());
        getCommand("day").setExecutor(new DayCycle());
        getCommand("noon").setExecutor(new DayCycle());
        getCommand("night").setExecutor(new DayCycle());
        getCommand("midnight").setExecutor(new DayCycle());
        getCommand("ping").setExecutor(new Ping());
        getCommand("corereload").setExecutor(this);

        //Events
        getServer().getPluginManager().registerEvents(new ProjectileRandomness(this), this);

    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "M" + ChatColor.WHITE + "C" + ChatColor.DARK_RED + "CORE " + ChatColor.RED + "DISABLED");
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && !(sender.hasPermission("sq.core.reload"))) {
            sender.sendMessage(ChatColor.RED+"You do not have permission to do this command!");
            return true;
        }
        reloadConfig();
        config = getConfig();
        setConfigVariables();
        sender.sendMessage(serverPrefix+ChatColor.GREEN+" Config successfully reloaded!");
        return true;
    }

    private void setConfigVariables(){
        projectileRandomness = getConfig().getBoolean("fixProjectileRandomness");
        connectToMongo = getConfig().getBoolean("connectToMongo");
    }
}
