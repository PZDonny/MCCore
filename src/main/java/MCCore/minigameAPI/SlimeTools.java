package MCCore.minigameAPI;

import com.infernalsuite.aswm.api.SlimePlugin;
import com.infernalsuite.aswm.api.loaders.SlimeLoader;
import org.bukkit.Bukkit;

public class SlimeTools {

    private static SlimePlugin slimePlugin;
    private static SlimeLoader slimeLoader;

    public static void setSlimeVariables(){
        slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        slimeLoader = slimePlugin.getLoader("file");
    }

    public static SlimePlugin getSlimePlugin(){
        return slimePlugin;
    }

    public static SlimeLoader getSlimeLoader(){
        return slimeLoader;
    }
}
