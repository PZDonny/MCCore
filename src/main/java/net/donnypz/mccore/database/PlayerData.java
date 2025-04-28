package net.donnypz.mccore.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Projections;
import net.donnypz.mccore.Core;
import net.donnypz.mccore.cosmetics.Cosmetic;
import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.utils.inventory.cosmetic.CosmeticShopUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    private static final HashMap<UUID, PlayerData> playerData = new HashMap<>();
    Document document;
    HashMap<String, UnlockedCosmetics> unlockedCosmetics = new HashMap<>();
    private final Bson unlockProjection = Projections.include(CosmeticShopUtils.PLAYER_UUID_FIELD, CosmeticShopUtils.COSMETIC_ID_FIELD);

    public PlayerData(@NotNull Player player, Document document){
        playerData.put(player.getUniqueId(), this);
        this.document = document;
        queryUnlockedCosmetics(player);
    }

    public Document getDocument() {
        return document;
    }

    public boolean hasCosmeticUnlocked(@NotNull Cosmetic cosmetic, @NotNull MongoCollection<Document> collection){
        String collectionNamespace = collection.getNamespace().getFullName();
        UnlockedCosmetics c = unlockedCosmetics.get(collectionNamespace);
        if (c == null){
            return false;
        }
        return c.contains(cosmetic);
    }

    public void addUnlockedCosmetic(@NotNull Cosmetic cosmetic, @NotNull MongoCollection<Document> collection){
        String collectionNamespace = collection.getNamespace().getFullName();
        unlockedCosmetics.get(collectionNamespace).add(cosmetic);
    }


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

    public int getUnlockedCosmetics(@NotNull CosmeticRegistry registry){
        MongoCollection<Document> coll = registry.getUnlockCollection();
        if (coll == null){
            return -1;
        }
        UnlockedCosmetics unlocked = unlockedCosmetics.get(coll.getNamespace().getFullName());
        return unlocked.getCount();
    }

    public static <T> T get(Player player, Class<T> clazz){
        return get(player.getUniqueId(), clazz);
    }

    public static <T> T get(UUID playerUUID, Class<T> clazz){
        return clazz.cast(playerData.get(playerUUID));
    }

    public static PlayerData get(Player player){
        return get(player.getUniqueId());
    }

    public static PlayerData get(UUID playerUUID){
        return playerData.get(playerUUID);
    }

    protected void remove(){
        for (UnlockedCosmetics c : unlockedCosmetics.values()){
            c.clear();
        }
        unlockedCosmetics.clear();
    }

    public static void remove(Player player){
        remove(player.getUniqueId());
    }

    public static void remove(UUID playerUUID){
        PlayerData data = playerData.remove(playerUUID);
        if (data != null) data.remove();
    }
}

