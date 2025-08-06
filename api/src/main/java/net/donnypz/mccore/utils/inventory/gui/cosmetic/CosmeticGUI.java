package net.donnypz.mccore.utils.inventory.gui.cosmetic;

import com.mongodb.client.MongoCollection;
import net.donnypz.mccore.database.PlayerData;
import net.donnypz.mccore.database.PlayerDatabaseUpdate;
import net.donnypz.mccore.utils.inventory.gui.ChestGUI;
import net.donnypz.mccore.utils.inventory.gui.GUIItem;
import net.donnypz.mccore.utils.item.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CosmeticGUI extends ChestGUI {

    final UUID playerUUID;
    final MongoCollection<Document> playerCollection;
    final MongoCollection<Document> unlockCollection;
    final String selectedField;
    String cosmeticTypeDisplayName;

    public CosmeticGUI(int rows,
                       @NotNull Component title,
                       @NotNull Player player,
                       @NotNull MongoCollection<Document> playerCollection,
                       @Nullable MongoCollection<Document> unlockCollection,
                       @NotNull String selectedField,
                       @NotNull String cosmeticTypeDisplayName) {
        super(rows, title);
        this.playerUUID = player.getUniqueId();
        this.playerCollection = playerCollection;
        this.unlockCollection = unlockCollection;
        this.selectedField = selectedField;
        this.cosmeticTypeDisplayName = cosmeticTypeDisplayName;
    }

    public CosmeticGUI setResetSlot(int slot, Object resetValue){
        ItemStack resetItem = new ItemBuilder(Material.STRUCTURE_VOID)
                .setDisplayName(MiniMessage.miniMessage().deserialize("<red>Reset <yellow>" + this.cosmeticTypeDisplayName))
                .addLoreLine(MiniMessage.miniMessage().deserialize("<gray>Click to reset your selected <yellow>" + this.cosmeticTypeDisplayName))
                .build();

        new GUIItem(this, slot, resetItem, (click) -> {
            Player player = (Player)click.getWhoClicked();
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Successfully reset <yellow>" + this.cosmeticTypeDisplayName + "<green> to default!"));
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1.0F, 2.0F);
            player.closeInventory();
            PlayerData playerData = PlayerData.get(playerUUID);
            if (playerData != null) {
                playerData.update((PlayerDatabaseUpdate) new PlayerDatabaseUpdate(playerCollection, playerUUID).setValue(selectedField, resetValue));
            }
        });
        return this;
    }
}
