package MCCore.listeners;

import MCCore.cosmetics.CosmeticLoader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;


public class ServerStart implements Listener {

    @EventHandler
    public void onServerStart(ServerLoadEvent event){
        if (event.getType() == ServerLoadEvent.LoadType.RELOAD) return;
        CosmeticLoader.loadAllLoaders();

    }
}
