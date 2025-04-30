package net.donnypz.mccore.database.cosmeticConditions;

import net.donnypz.mccore.cosmetics.Cosmetic;
import net.kyori.adventure.text.Component;
import org.bson.Document;

import java.util.UUID;

public interface CosmeticCondition {

    boolean meetsCondition(Document document, UUID playerUUID);

    Component buildLore(Cosmetic cosmetic);
}
