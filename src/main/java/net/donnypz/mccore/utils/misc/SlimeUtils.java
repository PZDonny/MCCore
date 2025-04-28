package net.donnypz.mccore.utils.misc;

import net.donnypz.mccore.Core;
import com.infernalsuite.aswm.api.AdvancedSlimePaperAPI;
import com.infernalsuite.aswm.api.exceptions.*;
import com.infernalsuite.aswm.api.loaders.SlimeLoader;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap;
import com.infernalsuite.aswm.loaders.file.FileLoader;
import com.infernalsuite.aswm.loaders.mongo.MongoLoader;
import net.donnypz.mccore.database.MongoUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class SlimeUtils {

    private static AdvancedSlimePaperAPI slimeAPI;
    private static SlimeLoader slimeLoader;

    public static boolean registerSlime(){
        if (slimeAPI == null){
            slimeAPI = AdvancedSlimePaperAPI.instance();
        }

        try{
            File slimeFile = new File(new File(".").getAbsolutePath(), "/slime_worlds/");
            if (!slimeFile.exists()){
                slimeFile.mkdirs();
            }
            slimeLoader = new FileLoader(new File("slime_worlds"));
            return slimeAPI != null;
        }
        catch(IllegalStateException | SecurityException e) {
            return false;
        }
    }

    public static AdvancedSlimePaperAPI getSlimeAPI(){
        if (!Core.isSlimeInstalled()) return null;
        return slimeAPI;
    }

    public static SlimeLoader getSlimeLoader(){
        if (!Core.isSlimeInstalled()){
            return null;
        }
        return slimeLoader;
    }


    public static void setFileLoader(){
        registerSlime();
    }

    public static void setMongoLoader(@NotNull String databaseName, @NotNull String collectionName){
        String uri = MongoUtils.getURI();
        slimeLoader = new MongoLoader(databaseName, collectionName, null, null, null, null, null, uri);
    }

    public static boolean isSlimeWorld(String worldName){
        return slimeAPI.getLoadedWorld(worldName) != null;
    }

    @ApiStatus.Internal
    public static @Nullable SlimeWorld getSlimeWorld(String worldName){
        return slimeAPI.getLoadedWorld(worldName);
    }

    @ApiStatus.Internal
    public static SlimeWorld getCloneFromLoader(String worldName, String cloneWorldName, boolean autoLoad){
        try{
            SlimeWorld world = slimeAPI.readWorld(slimeLoader, worldName, true, new SlimePropertyMap());
        //For servers w/o the template world (and must be retrieved from db)
            if (world == null){
                throw new RuntimeException("World does not exist: "+worldName);
            }

            try{
                SlimeWorld cloneWorld = world.clone(cloneWorldName, null);

                if (autoLoad){
                    Bukkit.getScheduler().runTask(Core.getInstance(), () ->{
                        slimeAPI.loadWorld(cloneWorld, true);
                    });
                }

                return cloneWorld;
            }
            catch (IllegalArgumentException | IOException | WorldAlreadyExistsException e) {
                throw new RuntimeException(e);
            }

        }
        catch (CorruptedWorldException | NewerFormatException | UnknownWorldException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
