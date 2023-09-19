package MCCore.listeners;

import MCCore.Core;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldLoad implements Listener {

//Protect Slime Worlds
    @EventHandler
    public void onWorldLoad(WorldLoadEvent e){
        World w = e.getWorld();
        Core.templateWorldSetup(w);
    }
}
