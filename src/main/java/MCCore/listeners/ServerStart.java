package MCCore.listeners;

import MCCore.Core;
import MCCore.MongoUtils;
import MCCore.cosmetics.CosmeticLoader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;


public class ServerStart implements Listener {
    @EventHandler
    public void onServerStart(ServerLoadEvent event){
        if (event.getType() == ServerLoadEvent.LoadType.RELOAD){
            return;
        }
        CosmeticLoader.loadAllLoaders();
        String cString = Core.getConnectionString();
        String mainDB = Core.getMainDatabaseName();
        String playtestDB = Core.getPlaytestDatabaseName();
        String minigameDB = Core.getMinigameDatabaseName();
        if (Core.isMongoAllowed()){
            MongoUtils.connectToMongo(cString, mainDB, playtestDB, minigameDB);
        }
    }
}
