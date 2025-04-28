package net.donnypz.mccore.cosmetics.conditions;

import net.donnypz.mccore.cosmetics.Cosmetic;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FieldMinimumCondition implements CosmeticCondition{
    Set<String> fields;
    String displayName;
    Number minimumValue;

    public FieldMinimumCondition(@NotNull Collection<String> fields, @NotNull String displayName, @NotNull Number minimumValue) {
        this.fields = new HashSet<>(fields);
        this.displayName = displayName;
        this.minimumValue = minimumValue;
    }

    public FieldMinimumCondition(@NotNull String field, @NotNull String displayName, @NotNull Number minimumValue) {
        this(Set.of(field), displayName, minimumValue);
    }

    public double getPlayerValueFromFields(Document document){
        double result = 0;
        for (String field : fields){
            result += ((Number) document.get(field)).doubleValue();
        }
        return result;
    }

    public String displayName() {
        return displayName;
    }

    public Set<String> getFields() {
        return new HashSet<>(fields);
    }

    public boolean hasField(String mongoField){
        return fields.contains(mongoField);
    }

    public boolean hasSingleField(){
        return fields.size() == 1;
    }

    public FieldMinimumCondition copy(Number minimumValue){
        return new FieldMinimumCondition(fields, displayName, minimumValue);
    }

    @Override
    public boolean meetsCondition(Document document, UUID playerUUID) {
        double result = 0;
        for (String field : fields){
            result += ((Number) document.get(field)).doubleValue();
        }
        return result >= minimumValue.doubleValue();
    }

    @Override
    public Component buildLore(Cosmetic cosmetic) {
        return Component.text("Requires: ", NamedTextColor.GRAY)
                .append(Component.text(String.valueOf(minimumValue), NamedTextColor.YELLOW))
                .append(Component.space())
                .append(Component.text(displayName, NamedTextColor.GRAY))
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}
