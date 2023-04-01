package MCCore.minigameAPI;

import com.infernalsuite.aswm.api.SlimePlugin;
import com.infernalsuite.aswm.api.loaders.SlimeLoader;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import org.bukkit.Bukkit;

import java.io.IOException;

public class SlimeTools {

    private static SlimePlugin slimePlugin;
    private static SlimeLoader slimeLoader;

    public static void setSlimeVariables(){
        slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        slimeLoader = slimePlugin.getLoader("mongodb");
    }

    public static SlimePlugin getSlimePlugin(){
        return slimePlugin;
    }

    public static SlimeLoader getSlimeLoader(){
        return slimeLoader;
    }

    public static boolean isSlimeWorld(String worldName){
        try{
            for (String world : getSlimeLoader().listWorlds()){
                if (world.equals(worldName)) return true;
            }
            return false;
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }

    }

}
