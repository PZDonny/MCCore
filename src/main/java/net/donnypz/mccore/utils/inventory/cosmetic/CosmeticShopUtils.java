package net.donnypz.mccore.utils.inventory.cosmetic;

import com.mongodb.client.MongoCollection;
import net.donnypz.mccore.Core;
import net.donnypz.mccore.cosmetics.Cosmetic;
import net.donnypz.playerdbutils.database.PlayerDatabaseUpdater;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

class CosmeticShopUtils {

    static void purchaseCosmetic(Player p, CosmeticGUI gui, Cosmetic cosmetic, Document document){
        int price = cosmetic.getPrice();
        p.sendMessage(Component.text("You have unlocked ",NamedTextColor.GREEN)
                .append(cosmetic.getCosmeticDisplayName())
                .append(Component.text("!", NamedTextColor.GREEN)));


        //Update existing player data
        PlayerDatabaseUpdater currencyUpdate = new PlayerDatabaseUpdater(gui.playerCollection, p.getUniqueId());
        currencyUpdate.incrementValue(cosmetic.getCurrencyField(), -price);
        currencyUpdate.update();

        insertCosmetic(gui, cosmetic, p.getUniqueId());

        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2f);
        p.closeInventory();
    }

    static void unlockCosmetic(Player p, CosmeticGUI gui, Cosmetic cosmetic){
        p.sendMessage(Component.text("You have unlocked ",NamedTextColor.GREEN)
                .append(cosmetic.getCosmeticDisplayName())
                .append(Component.text("!", NamedTextColor.GREEN)));

        insertCosmetic(gui, cosmetic, p.getUniqueId());
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2f);
        p.closeInventory();
    }

    private static void insertCosmetic(CosmeticGUI gui, Cosmetic cosmetic, UUID playerUUID){
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
            gui.unlockCollection.insertOne(new Document("uuid", playerUUID.toString())
                    .append(CosmeticGUI.COSMETIC_ID_FIELD, cosmetic.getValue()));
        });
    }



    static void notUnlocked(Player p){
        p.sendMessage(Component.text("You do not have that cosmetic unlocked", NamedTextColor.RED));
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        p.closeInventory();
    }


    static void alreadySelected(Player p){
        p.sendMessage(Component.text("You already have that cosmetic selected!", NamedTextColor.RED));
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
        p.closeInventory();
    }

    static void selectCosmetic(Player p, CosmeticGUI gui, Cosmetic cosmetic){
        p.sendMessage(Component.text("You have selected ", NamedTextColor.AQUA)
                .append(cosmetic.getCosmeticDisplayName())
                .append(Component.text("!", NamedTextColor.AQUA)));

        MongoCollection<Document> coll = gui.playerCollection;

        new PlayerDatabaseUpdater(coll, p.getUniqueId())
                .setValue(gui.selectedField, cosmetic.getValue())
                .update();
        p.closeInventory();
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2f);
    }
}
