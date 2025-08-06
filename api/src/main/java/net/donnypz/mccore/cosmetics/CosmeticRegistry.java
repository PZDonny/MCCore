package net.donnypz.mccore.cosmetics;

import com.mongodb.client.MongoCollection;
import net.donnypz.mccore.database.cosmeticConditions.CosmeticCondition;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;


/**
 * A registry class holding every cosmetic of a certain type {@code T}, and the associated collection that holds unlocked cosmetics.
 *
 * @param <T> the type of cosmetic this registry manages
 */
public abstract class CosmeticRegistry<T extends Cosmetic> {
    private static final HashSet<CosmeticRegistry<? extends Cosmetic>> registries = new HashSet<>();
    private final LinkedHashMap<String, T> cosmetics = new LinkedHashMap<>();
    private MongoCollection<Document> unlockCollection;
    Class<T> cosmeticType;

    public CosmeticRegistry(@NotNull Class<T> cosmeticType){
        this.cosmeticType = cosmeticType;
        registries.add(this);
    }

    protected abstract void registerCosmetics();

    CosmeticRegistry<T> registerCosmetic(T cosmetic){
        String cosmeticName = cosmetic.getCosmeticName();
        if (!cosmetics.containsKey(cosmeticName)){
            cosmetics.put(cosmeticName, cosmetic);
        }
        return this;
    }

    /**
     * Set the collection that will hold unlocked cosmetics of players. Only call this after the server has connected to MongoDB
     * @param unlockCollection the collection to assoiciate with this registry
     */
    public void setUnlockCollection(MongoCollection<Document> unlockCollection){
        this.unlockCollection = unlockCollection;
    }

    public T getCosmetic(@NotNull String cosmeticName){
        return cosmetics.get(cosmeticName);
    }

    public List<T> getCosmetics(){
        return new ArrayList<>(this.cosmetics.sequencedValues());
    }




    /**
     * Get cosmetics that contain the provided {@link CosmeticCondition}
     * @param currencyField the cosmetic condition
     * @return a list of cosmetics
     */
    public List<T> getCosmeticsFromCurrencyField(@NotNull String currencyField){
        List<T> cosmetics = new ArrayList<>();

        for (T cosmetic : this.cosmetics.values()){
            if (cosmetic.hasCurrencyField(currencyField)) {
                cosmetics.add(cosmetic);
            }
        }
        return cosmetics;
    }

    /**
     * Get cosmetics that contain the provided {@link CosmeticCondition}
     * @param cosmeticCondition the cosmetic condition
     * @return a list of cosmetics
     */
    public List<T> getCosmetics(@NotNull CosmeticCondition cosmeticCondition){
        List<T> cosmetics = new ArrayList<>();

        for (T cosmetic : this.cosmetics.values()){
            if (cosmetic.hasCondition(cosmeticCondition)) {
                cosmetics.add(cosmetic);
            }
        }
        return cosmetics;
    }


    /**
     * Get cosmetics that contain at least one of the provided {@link CosmeticCondition}
     * @param cosmeticConditions the cosmetic conditions
     * @return a list of cosmetics
     */
    public List<T> getCosmetics(CosmeticCondition... cosmeticConditions){
        List<T> cosmetics = new ArrayList<>();

        for (T cosmetic : this.cosmetics.values()){
            for (CosmeticCondition condition : cosmeticConditions){
                if (cosmetic.hasCondition(condition)) {
                    cosmetics.add(cosmetic);
                    break;
                }
            }

        }
        return cosmetics;
    }

    public SequencedSet<String> getCosmeticNames(){
        return new LinkedHashSet<>(cosmetics.sequencedKeySet());
    }

    public MongoCollection<Document> getUnlockCollection(){
        return unlockCollection;
    }

    @ApiStatus.Internal
    public static void loadRegistries(){
        for (CosmeticRegistry<?> registry : registries){
            try{
                registry.registerCosmetics();
            }
            catch(IllegalStateException e){
                Bukkit.getLogger().warning("Failed to register a cosmetic registry! Was a plugin disabled?");
            }
        }
    }

    public static HashSet<CosmeticRegistry<? extends Cosmetic>> getRegistries(){
        return registries;
    }
}
