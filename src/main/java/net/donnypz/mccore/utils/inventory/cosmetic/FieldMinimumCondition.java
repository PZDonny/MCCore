package net.donnypz.mccore.utils.inventory.cosmetic;

import net.donnypz.mccore.cosmetics.Cosmetic;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FieldMinimumCondition {
    Set<String> fields;
    String conditionDisplayName;
    private final HashMap<Cosmetic, Number> minimumValues = new HashMap<>();

    public FieldMinimumCondition(Collection<String> fields, String conditionDisplayName) {
        this.fields = new HashSet<>(fields);
        this.conditionDisplayName = conditionDisplayName;
    }

    public FieldMinimumCondition(String field, String conditionDisplayName) {
        this.fields = Set.of(field);
        this.conditionDisplayName = conditionDisplayName;
    }

    public double getPlayerValueFromFields(Document document){
        double result = 0;
        for (String field : fields){
            result += ((Number) document.get(field)).doubleValue();
        }
        return result;
    }

    public FieldMinimumCondition addCosmetic(Cosmetic cosmetic, Number minimumValue){
        minimumValues.put(cosmetic, minimumValue);
        cosmetic.addFieldMinimumCondition(this);
        return this;
    }

    public boolean contains(Cosmetic cosmetic){
        return minimumValues.containsKey(cosmetic);
    }

    public String conditionDisplayName() {
        return conditionDisplayName;
    }

    public @Nullable Number getMinimumValue(Cosmetic cosmetic) {
        return minimumValues.get(cosmetic);
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
}
