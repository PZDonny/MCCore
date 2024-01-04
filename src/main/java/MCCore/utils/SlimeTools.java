package MCCore.utils;

import MCCore.Core;
import com.infernalsuite.aswm.api.SlimePlugin;
import com.infernalsuite.aswm.api.exceptions.*;
import com.infernalsuite.aswm.api.loaders.SlimeLoader;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public class SlimeTools {

    private static SlimePlugin slimePlugin;
    private static SlimeLoader slimeLoader;

    public static boolean registerSlime(){
        if (Core.isSlimeInstalled()){
            slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
            slimeLoader = slimePlugin.getLoader("mongodb");
            if (slimeLoader == null){
                return false;
            }
            return true;
        }
        return false;

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

    public static SlimeWorld getCloneFromDataSource(String worldName, String cloneWorldName, boolean autoLoad){
        SlimeWorld world = slimePlugin.getWorld(worldName);
        if (world == null){
            try{
                //Fetches and loads from loader
                world = slimePlugin.loadWorld(slimeLoader, worldName, true, new SlimePropertyMap());
                if (world == null){
                    throw new UnknownWorldException(worldName);
                }
            }
            catch (WorldLockedException | IOException | UnknownWorldException | NewerFormatException |
                   CorruptedWorldException e) {
                throw new RuntimeException(e);
            }
        }

        try{
            SlimeWorld cloneWorld = world.clone(cloneWorldName, null);

        //Unload Template
            //WorldTools.destroyWorld(Bukkit.getWorld(worldName));



        //Auto Load Clone
            if (autoLoad){
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        slimePlugin.loadWorld(cloneWorld);
                        World w = Bukkit.getWorld(cloneWorldName);
                        if (w != null){
                            w.setKeepSpawnInMemory(false);
                            w.setAutoSave(false);
                        }
                    }
                }.runTask(Core.getInstance());


            }

            return cloneWorld;
        }
        catch (IOException | WorldAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
    }
}
