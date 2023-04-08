package MCCore.utils;

import MCCore.Core;
import com.infernalsuite.aswm.api.SlimePlugin;
import com.infernalsuite.aswm.api.loaders.SlimeLoader;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import org.bukkit.Bukkit;

import java.io.IOException;

public class SlimeTools {

    private static SlimePlugin slimePlugin;
    private static SlimeLoader slimeLoader;

    public static void setSlimeVariables(){
        if (Core.isSlimeInstalled()){
            slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
            slimeLoader = slimePlugin.getLoader("mongodb");
        }

    }

    public static SlimePlugin getSlimePlugin(){
        if (!Core.isSlimeInstalled()) return null;
        return slimePlugin;
    }

    public static SlimeLoader getSlimeLoader(){
        if (!Core.isSlimeInstalled()) return null;
        return slimeLoader;
    }

    public static boolean isSlimeWorld(String worldName){
        if (!Core.isSlimeInstalled()) return false;
        try{
            for (String world : slimeLoader.listWorlds()){
                if (world.equals(worldName)) return true;
            }
            return false;
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }

    }

}
