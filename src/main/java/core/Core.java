package core;

import core.ConnectDB.coreMongo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Core extends JavaPlugin {



    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Mine" + ChatColor.WHITE + "Classic" + ChatColor.DARK_RED + " CORE " + ChatColor.GREEN + "ENABLED");
        new BukkitRunnable() {//Connect to Mongo Asynchronously
            public void run() {
                coreMongo.connectToMongo();
            }
        }.runTaskAsynchronously(this);

    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Mine" + ChatColor.WHITE + "Classic" + ChatColor.DARK_RED + " CORE " + ChatColor.RED + "DISABLED");
        // Plugin shutdown logic
    }


}
