package net.donnypz.mccore.version;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigOptions {
    public final boolean connectToMongo;
    public final String connectionString;
    public final boolean projectileRandomness;
    public final boolean isMinigameEnabled;
    public final World waitingWorld;
    public final boolean waitingWorldLimited;
    public final boolean hideConnectionMessage;


    public ConfigOptions(FileConfiguration config){
        connectToMongo = config.getBoolean("mongoDB.enabled");
        connectionString = config.getString("mongoDB.connectionString");
        projectileRandomness = config.getBoolean("fixProjectileRandomness");
        isMinigameEnabled = config.getBoolean("minigames.enabled");

        //Minigames
        if (!isMinigameEnabled){
            waitingWorld = null;
            waitingWorldLimited = false;
            hideConnectionMessage = false;
            return;
        }

        waitingWorldLimited = config.getBoolean("minigames.waitingWorld");
        hideConnectionMessage = config.getBoolean("minigames.hideConnectionMessage");

        String world = config.getString("minigames.waitingWorld");
        if (world != null){
            World w = Bukkit.getWorld(world);
            if (w != null){
                waitingWorld = w;
            }
            else{
                Bukkit.getLogger().warning("There was an error using the specified minigame waiting world! ("+world+")");
                waitingWorld = null;
            }
        }
        else{
            waitingWorld = null;
        }
    }
}
