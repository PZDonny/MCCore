package net.donnypz.mccore.version;

import net.donnypz.mccore.cosmetics.preset.armortrims.TrimMaterialCosmetic;
import net.donnypz.mccore.cosmetics.preset.armortrims.TrimPatternCosmetic;
import net.donnypz.mccore.utils.item.ItemHandler;
import net.donnypz.mccore.utils.slime.WrappedSlimeWorld;
import org.bukkit.plugin.java.JavaPlugin;

public class CoreAPI {
    static VersionHandler versionHandler;
    static ItemHandler itemHandler;
    static Class<WrappedSlimeWorld<?>> wrappedSlimeWorldClass;
    static Class<WrappedSlimeWorld<?>> wrappedSlimeLoaderClass;
    static JavaPlugin javaPlugin;
    static ConfigOptions configOptions;

    static TrimPatternCosmetic[] trimPatternCosmetics;
    static TrimMaterialCosmetic[] trimMaterialCosmetics;

    public static VersionHandler getVersionHandler(){
        return versionHandler;
    }

    public static ItemHandler getItemHandler(){
        return itemHandler;
    }

    public static JavaPlugin getPlugin(){
        return javaPlugin;
    }

    public static ConfigOptions getConfigOptions(){
        return configOptions;
    }

    public static TrimPatternCosmetic[] getTrimPatternCosmetic(){
        return trimPatternCosmetics;
    }

    public static TrimMaterialCosmetic[] getTrimMaterialCosmetics(){
        return trimMaterialCosmetics;
    }
}
