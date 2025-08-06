package net.donnypz.mccore.utils.slime;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SlimeUtils {

    private static WrappedSlimeLoader<?> slimeLoader;
    private static SlimeHandler<?, ?> slimeHandler;


    public static SlimeHandler<?, ?> getHandler(){
        return slimeHandler;
    }

    public static boolean isSlimeInstalled(){
        return slimeHandler != null;
    }

    public static boolean registerSlime(SlimeHandler<?, ?> handler){
        SlimeUtils.slimeHandler = handler;

        try{
            File slimeFile = new File(new File(".").getAbsolutePath(), "/slime_worlds/");
            if (!slimeFile.exists()){
                slimeFile.mkdirs();
            }
            slimeLoader = slimeHandler.createFileLoader(slimeFile);
            return true;
        }
        catch(IllegalStateException | SecurityException e) {
            return false;
        }
    }

    public static void setDefaultLoader(@NotNull WrappedSlimeLoader<?> loader){
        slimeLoader = loader;
    }


    public static WrappedSlimeLoader<?> getDefaultLoader(){
        return slimeLoader;
    }


    public static boolean isSlimeWorld(@NotNull String worldName){
        return slimeHandler.isSlimeWorld(worldName);
    }

    @ApiStatus.Internal
    public static @Nullable WrappedSlimeWorld<?> getSlimeWorld(@NotNull String worldName){
        return slimeHandler.getSlimeWorld(worldName);
    }

    public static WrappedSlimeWorld<?> getCloneFromLoader(@NotNull String worldName, @NotNull String cloneWorldName, boolean autoLoad){
        return slimeHandler.getCloneFromLoader(worldName, cloneWorldName, autoLoad);
    }
}
