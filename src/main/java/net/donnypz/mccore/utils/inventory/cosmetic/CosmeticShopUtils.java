package net.donnypz.mccore.utils.inventory.cosmetic;

import com.mongodb.client.MongoCollection;
import net.donnypz.mccore.Core;
import net.donnypz.mccore.cosmetics.Cosmetic;
import net.donnypz.mccore.database.PlayerData;
import net.donnypz.mccore.database.PlayerDatabaseUpdate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class CosmeticShopUtils {

    public static final String PLAYER_UUID_FIELD = "player_uuid";
    public static final String COSMETIC_ID_FIELD = "cosmetic_id";
    static final String UNLOCK_TIME_FIELD = "unlock_time";

    static void purchaseCosmetic(Player p, CosmeticGUI gui, PlayerData playerData, Cosmetic cosmetic, Cosmetic.Currency currency){
        p.sendMessage(Component.text("You have unlocked ",NamedTextColor.GREEN)
                .append(cosmetic.getCosmeticDisplayName())
                .append(Component.text("!", NamedTextColor.GREEN)));

        //Update existing player data
        new PlayerDatabaseUpdate(gui.playerCollection, p.getUniqueId())
                .incrementValue(currency.currencyField(), -currency.price())
                .update(playerData.getDocument());

        insertCosmetic(gui, cosmetic, p.getUniqueId());
        close(p, 2f);
    }

    static void unlockCosmetic(Player p, CosmeticGUI gui, Cosmetic cosmetic){
        p.sendMessage(Component.text("You have unlocked ",NamedTextColor.GREEN)
                .append(cosmetic.getCosmeticDisplayName())
                .append(Component.text("!", NamedTextColor.GREEN)));


        insertCosmetic(gui, cosmetic, p.getUniqueId());
        close(p, 2f);
    }

    static void notUnlocked(Player p){
        p.sendMessage(Component.text("You do not have that cosmetic unlocked", NamedTextColor.RED));
        close(p, 0.5f);
    }


    static void alreadySelected(Player p){
        p.sendMessage(Component.text("You already have that cosmetic selected!", NamedTextColor.RED));
        close(p, 0.5f);
    }

    static void selectCosmetic(Player p, CosmeticGUI gui, PlayerData playerData, Cosmetic cosmetic){
        p.sendMessage(Component.text("You have selected ", NamedTextColor.AQUA)
                .append(cosmetic.getCosmeticDisplayName())
                .append(Component.text("!", NamedTextColor.AQUA)));

        MongoCollection<Document> coll = gui.playerCollection;

        new PlayerDatabaseUpdate(coll, p.getUniqueId())
                .setValue(gui.selectedField, cosmetic.getSelectValue())
                .update(playerData.getDocument());
        close(p, 2f);
    }

    //Add Cosmetic to its designated cosmetic collection (table)
    private static void insertCosmetic(CosmeticGUI gui, Cosmetic cosmetic, UUID playerUUID){
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
            PlayerData playerData = PlayerData.get(playerUUID);
            playerData.addUnlockedCosmetic(cosmetic, gui.unlockCollection);

            gui.unlockCollection
                    .insertOne(
                            new Document(PLAYER_UUID_FIELD, playerUUID.toString())
                            .append(COSMETIC_ID_FIELD, cosmetic.getSelectValue())
                                    .append(UNLOCK_TIME_FIELD, new Date()));
        });
    }

    private static void close(Player p, float soundPitch){
        p.closeInventory();
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, soundPitch);
    }
}
