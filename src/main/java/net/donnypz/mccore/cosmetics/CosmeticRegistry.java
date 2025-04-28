package net.donnypz.mccore.cosmetics;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.donnypz.mccore.cosmetics.conditions.CosmeticCondition;
import net.donnypz.mccore.database.MongoUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public abstract class CosmeticRegistry {

    protected static final HashSet<CosmeticRegistry> registries = new HashSet<>();
    private final LinkedHashMap<String, Cosmetic> cosmeticStorage = new LinkedHashMap<>();
    private MongoCollection<Document> unlockCollection;

    public CosmeticRegistry(){
        registries.add(this);
    }

    protected abstract void registerCosmetics();

    Cosmetic registerCosmetic(Cosmetic cosmetic){
        String cosmeticName = cosmetic.getCosmeticName();
        if (!cosmeticStorage.containsKey(cosmeticName)){
            cosmeticStorage.put(cosmeticName, cosmetic);
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
        return cosmeticStorage.get(cosmeticName);
    }

    public <T> T getCosmetic(@NotNull String cosmeticName, Class<T> cosmeticClass){
        Cosmetic cosmetic = cosmeticStorage.get(cosmeticName);
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

        for (Cosmetic cosmetic : cosmeticStorage.values()){
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

        for (Cosmetic cosmetic : cosmeticStorage.values()){
            for (CosmeticCondition condition : cosmeticConditions){
                if (cosmetic.hasCondition(condition)) {
                    cosmetics.add(cosmeticClass.cast(cosmetic));
                    break;
                }
            }

        }
        return cosmetics;
    }


    public <T> List<T> getCosmetics(Class<T> cosmeticClass){
        try{
            List<T> list = new ArrayList<>();
            for (Cosmetic cosmetic : cosmeticStorage.values()){
                list.add(cosmeticClass.cast(cosmetic));
            }
            return list;
        }
        catch(ClassCastException e){
            throw new ClassCastException("Invalid cosmetic class provided");
        }
    }

    public List<Cosmetic> getCosmetics(){
        return new ArrayList<>(cosmeticStorage.values());
    }

    public SequencedSet<String> getCosmeticNames(){
        return cosmeticStorage.sequencedKeySet();
    }

    public MongoCollection<Document> getUnlockCollection(){
        return unlockCollection;
    }

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
