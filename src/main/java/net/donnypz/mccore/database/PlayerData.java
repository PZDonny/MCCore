package net.donnypz.mccore.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Projections;
import net.donnypz.mccore.Core;
import net.donnypz.mccore.cosmetics.Cosmetic;
import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.utils.inventory.gui.cosmetic.CosmeticShopUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Holds a player's MongoDB Document and their unlocked cosmetics
 */
public class PlayerData {

    private static final HashMap<UUID, PlayerData> playerData = new HashMap<>();
    private final UUID playerUUID;
    private final Document document;
    HashMap<String, UnlockedCosmetics> unlockedCosmetics = new HashMap<>();
    private final Bson unlockProjection = Projections.include(CosmeticShopUtils.PLAYER_UUID_FIELD, CosmeticShopUtils.COSMETIC_ID_FIELD);

    /**
     * Create {@link PlayerData} for a player, typically after they join the server
     * @param player the player
     * @param document the player's document
     */
    public PlayerData(@NotNull Player player, @NotNull Document document){
        this.playerUUID = player.getUniqueId();
        this.document = document;
        playerData.put(playerUUID, this);
        queryUnlockedCosmetics(player);
    }

    /**
     * Get the UUID of the player that this represents
     * @return a UUID
     */
    public @NotNull UUID getUUID(){
        return playerUUID;
    }

    /**
     * Get the player's MongoDB Document
     * @return a {@link Document}
     */
    public @NotNull Document getDocument() {
        return document;
    }

    /**
     * Check if a player has a cosmetic unlocked
     * @param cosmetic the cosmetic
     * @param unlockCollection the collection that holds documents for the cosmetic
     * @return a boolean
     */
    public boolean hasCosmeticUnlocked(@NotNull Cosmetic cosmetic, @NotNull MongoCollection<Document> unlockCollection){
        String collectionNamespace = unlockCollection.getNamespace().getFullName();
        UnlockedCosmetics c = unlockedCosmetics.get(collectionNamespace);
        if (c == null){
            return false;
        }
        return c.contains(cosmetic);
    }

    /**
     * Recognize a cosmetic as unlocked for this player's data.
     * This does not send an update to the database, as the update should be performed before this is called.
     * @param cosmetic the unlocked cosmetic
     * @param unlockCollection the collection that holds documents for the cosmetic
     */
    @ApiStatus.Internal
    public void addUnlockedCosmetic(@NotNull Cosmetic cosmetic, @NotNull MongoCollection<Document> unlockCollection){
        String collectionNamespace = unlockCollection.getNamespace().getFullName();
        unlockedCosmetics.get(collectionNamespace).add(cosmetic);
    }


    /**
     * Asynchronously query the cosmetics a player has unlocked, and cache the returned data
     * @param player
     */
    private void queryUnlockedCosmetics(Player player){
        new BukkitRunnable(){
            final Document filterDoc = new Document(CosmeticShopUtils.PLAYER_UUID_FIELD, player.getUniqueId().toString());

            @Override
            public void run() {
                for (CosmeticRegistry registry : CosmeticRegistry.getRegistries()){
                    //Prematurely stop if player is no longer connected
                    if (!player.isConnected()){
                        return;
                    }
                    MongoCollection<Document> unlockCollection = registry.getUnlockCollection();
                    if (unlockCollection == null){
                        continue;
                    }
                    String collectionNamespace = unlockCollection.getNamespace().getFullName();
                    UnlockedCosmetics uc = new UnlockedCosmetics();
                    unlockedCosmetics.put(collectionNamespace, uc);

                    unlockCollection
                            .find(filterDoc)
                            .projection(unlockProjection)
                            .forEach(d -> {
                                String cosmeticID = d.getString(CosmeticShopUtils.COSMETIC_ID_FIELD);
                                Cosmetic cosmetic = registry.getCosmetic(cosmeticID);
                                if (cosmetic != null){
                                    uc.add(cosmetic);
                                }
                            });
                }
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    /**
     * Get the number of cosmetics a player has unlocked based on its registry's unlock collection
     * @param registry the cosmetic registry with an non-null unlock collection
     * @return an integer
     */
    public int getUnlockedCosmetics(@NotNull CosmeticRegistry registry){
        MongoCollection<Document> coll = registry.getUnlockCollection();
        if (coll == null){
            return -1;
        }
        return getUnlockedCosmetics(coll);
    }

    /**
     * Get the number of cosmetic a player has unlocked based on its unlock collection
     * @param unlockCollection the collection that holds documents for the cosmetic
     * @return an integer
     */
    public int getUnlockedCosmetics(@NotNull MongoCollection<Document> unlockCollection){
        UnlockedCosmetics unlocked = unlockedCosmetics.get(unlockCollection.getNamespace().getFullName());
        return unlocked.getCount();
    }

    /**
     * Get a player's {@link PlayerData}
     * @param player
     * @param clazz
     * @return {@link PlayerData} if the player is online
     * @param <T>
     */
    public static <T> T get(@NotNull Player player, Class<T> clazz){
        return get(player.getUniqueId(), clazz);
    }

    public static <T> T get(@NotNull UUID playerUUID, Class<T> clazz){
        return clazz.cast(playerData.get(playerUUID));
    }

    public static PlayerData get(@NotNull Player player){
        return get(player.getUniqueId());
    }

    public static PlayerData get(@NotNull UUID playerUUID){
        return playerData.get(playerUUID);
    }

    protected void remove(){
        for (UnlockedCosmetics c : unlockedCosmetics.values()){
            c.clear();
        }
        unlockedCosmetics.clear();
    }

    /**
     * Remove a player's {@link PlayerData}, typically after they disconnect.
     * @param player the player
     */
    public static void remove(Player player){
        remove(player.getUniqueId());
    }

    /**
     * Remove a player's {@link PlayerData}, typically after they disconnect.
     * @param playerUUID the player's uuid
     */
    public static void remove(UUID playerUUID){
        PlayerData data = playerData.remove(playerUUID);
        if (data != null) data.remove();
    }

    static class UnlockedCosmetics {

        private final HashSet<Cosmetic> cosmetics = new HashSet<>();

        void add(@NotNull Cosmetic cosmetic){
            cosmetics.add(cosmetic);
        }

        boolean contains(@NotNull Cosmetic cosmetic){
            return cosmetics.contains(cosmetic);
        }

        int getCount(){
            return cosmetics.size();
        }

        void clear(){
            cosmetics.clear();
        }

    }
}

