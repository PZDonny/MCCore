package net.donnypz.mccore;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

final class ConfigLoader {
    
    private ConfigLoader(){}
    
    static void loadConfig(){
        FileConfiguration config = Core.getInstance().getConfig();
    //MongoDB
        Core.connectToMongo = config.getBoolean("mongoDB.enabled");
        Core.connectionString = config.getString("mongoDB.connectionString");


        Core.isChatCooldownEnabled = config.getBoolean("chatCooldownEnabled");
        Core.projectileRandomness = config.getBoolean("fixProjectileRandomness");

        //Minigames
        Core.isMinigameEnabled = config.getBoolean("minigames.enabled");
        if (!Core.isMinigameEnabled){
            Core.waitingWorld = null;
            return;
        }

        Core.waitingWorldLimited = config.getBoolean("minigames.limitPlayer");
        Core.hideConnectionMessage = config.getBoolean("minigames.hideConnectionMessage");

        String world = config.getString("minigames.waitingWorld");
        if (world != null && Bukkit.getWorld(world) != null){
            Core.waitingWorld = Bukkit.getWorld(world);
        }
        else{
            Core.waitingWorld = null;
            Bukkit.getServer().getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("<red>There was an error using the specified minigame waiting world! <gold>("+world+")"));
        }
    }
}
