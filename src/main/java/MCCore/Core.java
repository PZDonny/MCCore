package MCCore;

import MCCore.commands.*;
import MCCore.events.ProjectileRandomness;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.UUID;

public final class Core extends JavaPlugin {

    private final String serverPrefix = ChatColor.GREEN+"Mine"+ChatColor.WHITE+"Classic";
    //public static WorldGuardPlugin WGAPI;
    @Override
    public void onEnable() {
        //WGAPI = getWorldGuard();
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Mine" + ChatColor.WHITE + "Classic" + ChatColor.DARK_RED + " CORE " + ChatColor.GREEN + "ENABLED");
        new BukkitRunnable() {//Connect to Mongo Asynchronously
            public void run() {
                connectDB.connectToMongo();
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

        //Events
        getServer().getPluginManager().registerEvents(new ProjectileRandomness(this), this);

    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Mine" + ChatColor.WHITE + "Classic" + ChatColor.DARK_RED + " CORE " + ChatColor.RED + "DISABLED");
        // Plugin shutdown logic
    }

    private WorldGuardPlugin getWorldGuard(){
        Plugin WAPI = this.getServer().getPluginManager().getPlugin("WorldGuard");
        if (!(WAPI instanceof WorldGuardPlugin)) return null;
        return (WorldGuardPlugin) WAPI;
    }


}
