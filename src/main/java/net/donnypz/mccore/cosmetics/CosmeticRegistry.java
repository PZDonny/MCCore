package net.donnypz.mccore.cosmetics;

import com.mongodb.client.MongoCollection;
import net.donnypz.mccore.database.cosmeticConditions.CosmeticCondition;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;


/**
 * A registry class holding every cosmetic of a certain type, and the associated collection that holds unlocked cosmetics
 */
public abstract class CosmeticRegistry {
    private static final HashSet<CosmeticRegistry> registries = new HashSet<>();
    private final LinkedHashMap<String, Cosmetic> cosmetics = new LinkedHashMap<>();
    private MongoCollection<Document> unlockCollection;

    public CosmeticRegistry(){
        registries.add(this);
    }

    protected abstract void registerCosmetics();

    Cosmetic registerCosmetic(Cosmetic cosmetic){
        String cosmeticName = cosmetic.getCosmeticName();
        if (!cosmetics.containsKey(cosmeticName)){
            cosmetics.put(cosmeticName, cosmetic);
        }
        return cosmetic;
    }

    /**
     * Set the collection that will hold unlocked cosmetics of players. Only call this after the server has connected to MongoDB
     * @param unlockCollection the collection to assoiciate with this registry
     */
    public void setUnlockCollection(MongoCollection<Document> unlockCollection){
        this.unlockCollection = unlockCollection;
    }

    public Cosmetic getCosmetic(@NotNull String cosmeticName){
        return cosmetics.get(cosmeticName);
    }

    public <T> T getCosmetic(@NotNull String cosmeticName, Class<T> cosmeticClass){
        Cosmetic cosmetic = cosmetics.get(cosmeticName);
        if (cosmetic == null){
            return null;
        }
        try{
            return cosmeticClass.cast(cosmetic);
        }
        catch(ClassCastException e){
            throw new ClassCastException("Invalid cosmetic class provided");
        }
    }


    /**
     * Get cosmetics that contain the provided {@link CosmeticCondition}
     * @param cosmeticCondition
     * @return a list of cosmetics
     */
    public List<Cosmetic> getCosmetics(CosmeticCondition cosmeticCondition){
        return getCosmetics(cosmeticCondition, Cosmetic.class);
    }

    /**
     * Get cosmetics that contain the provided {@link CosmeticCondition}
     * @param cosmeticCondition
     * @param cosmeticClass
     * @return a list of cosmetics
     */
    public <T> List<T> getCosmetics(CosmeticCondition cosmeticCondition, Class<T> cosmeticClass){
        List<T> cosmetics = new ArrayList<>();

        for (Cosmetic cosmetic : this.cosmetics.values()){
            if (cosmetic.hasCondition(cosmeticCondition)) {
                cosmetics.add(cosmeticClass.cast(cosmetic));
            }
        }
        return cosmetics;
    }


    /**
     * Get cosmetics that contain at least one of the provided {@link CosmeticCondition}
     * @param cosmeticConditions
     * @return a list of cosmetics
     */
    public List<Cosmetic> getCosmetics(CosmeticCondition... cosmeticConditions){
        return getCosmetics(Cosmetic.class, cosmeticConditions);
    }

    /**
     * Get cosmetics that contain at least one of the provided {@link CosmeticCondition}
     * @param cosmeticConditions
     * @param cosmeticClass
     * @return a list of cosmetics
     */
    public <T> List<T> getCosmetics(Class<T> cosmeticClass, CosmeticCondition... cosmeticConditions){
        List<T> cosmetics = new ArrayList<>();

        for (Cosmetic cosmetic : this.cosmetics.values()){
            for (CosmeticCondition condition : cosmeticConditions){
                if (cosmetic.hasCondition(condition)) {
                    cosmetics.add(cosmeticClass.cast(cosmetic));
                    break;
                }
            }

        }
        return cosmetics;
    }


    public <T> List<T> getCosmetics(@NotNull Class<T> cosmeticClass){
        try{
            List<T> list = new ArrayList<>();
            for (Cosmetic cosmetic : cosmetics.values()){
                list.add(cosmeticClass.cast(cosmetic));
            }
            return list;
        }
        catch(ClassCastException e){
            throw new ClassCastException("Invalid cosmetic class provided");
        }
    }

    public List<Cosmetic> getCosmetics(){
        return new ArrayList<>(cosmetics.values());
    }

    public SequencedSet<String> getCosmeticNames(){
        return cosmetics.sequencedKeySet();
    }

    public MongoCollection<Document> getUnlockCollection(){
        return unlockCollection;
    }

    @ApiStatus.Internal
    public static void loadRegistries(){
        for (CosmeticRegistry registry : registries){
            try{
                registry.registerCosmetics();
            }
            catch(IllegalStateException e){
                Bukkit.getLogger().warning("Failed to register a cosmetic registry! Was a plugin disabled?");
            }
        }
    }

    public static HashSet<CosmeticRegistry> getRegistries(){
        return registries;
    }
}
