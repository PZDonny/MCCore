package net.donnypz.mccore.version;

import net.donnypz.mccore.cosmetics.preset.armortrims.TrimMaterialCosmetic;
import net.donnypz.mccore.cosmetics.preset.armortrims.TrimPatternCosmetic;
import net.donnypz.mccore.utils.item.ItemHandler;
import net.donnypz.mccore.utils.slime.SlimeHandler;
import net.donnypz.mccore.utils.slime.SlimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class VersionHandlerRegistry {
    static final HashMap<String, String> mainVersion = new HashMap<>();
    private static final String prefix = "net.donnypz.mccore.version.v";
    static{
        mainVersion.put("1.21", "1_21_1");
        mainVersion.put("1.21.2", "1_21_3");
        mainVersion.put("1.21.7", "1_21_8");
    }


    public static boolean init(JavaPlugin plugin){
        String version = getServerVersion();
        String fullPrefix = prefix+version;
        try{
            Class<?> versionClazz = Class.forName(fullPrefix + ".VersionHandlerImpl");
            CoreAPI.versionHandler = (VersionHandler) versionClazz.getDeclaredConstructor().newInstance();

            Class<?> itemClazz = Class.forName(fullPrefix + ".ItemHandlerImpl");
            CoreAPI.itemHandler = (ItemHandler) itemClazz.getDeclaredConstructor().newInstance();

            Class<?> slimeClazz = Class.forName(fullPrefix + ".SlimeHandlerImpl");
            SlimeHandler<?, ?> slimeHandler = (SlimeHandler<?, ?>) slimeClazz.getDeclaredConstructor().newInstance();
            if (!SlimeUtils.registerSlime(slimeHandler)){
                Bukkit.getLogger().warning("Failed to register Slime");
            }

            Class<Enum> enumTrimMat = (Class<Enum>) Class.forName(fullPrefix + ".TrimMaterialCosmeticImpl");
            //TrimMaterialCosmetic trimMatImpl = (TrimMaterialCosmetic) clazzTrimMat.getDeclaredConstructor().newInstance();
            CoreAPI.trimMaterialCosmetics = (TrimMaterialCosmetic[]) enumTrimMat.getEnumConstants();

            Class<Enum> enumTrimPat = (Class<Enum>) Class.forName(fullPrefix + ".TrimPatternCosmeticImpl");
            //TrimPatternCosmetic trimPatImpl = (TrimPatternCosmetic) clazzTrimPat.getDeclaredConstructor().newInstance();
            CoreAPI.trimPatternCosmetics = (TrimPatternCosmetic[]) enumTrimPat.getEnumConstants();
        }
        catch(ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
              NoSuchMethodException ex){
            ex.printStackTrace();
            return false;
        }
        CoreAPI.javaPlugin = plugin;
        return true;
    }

    public static void updateConfig(){
        CoreAPI.configOptions = new ConfigOptions(CoreAPI.getPlugin().getConfig());
    }

    private static String getServerVersion(){
        String mcVer = Bukkit.getMinecraftVersion();
        return mainVersion.getOrDefault(mcVer, mcVer.replace(".", "_"));
    }
}
