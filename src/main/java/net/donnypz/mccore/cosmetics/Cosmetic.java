package net.donnypz.mccore.cosmetics;

import net.donnypz.mccore.utils.inventory.cosmetic.DocumentCountCondition;
import net.donnypz.mccore.utils.inventory.cosmetic.FieldMinimumCondition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public abstract class Cosmetic{
    private final String cosmeticName;
    private Object value;
    private Component cosmeticDisplayName = Component.empty();
    private String currencyField;
    private int price = -1;
    private String permission;
    private final HashSet<FieldMinimumCondition> fieldMinimumConditions = new HashSet<>();
    private final HashSet<DocumentCountCondition> documentCountConditions = new HashSet<>();

    private Material displayMaterial;

    public Cosmetic(String cosmeticName, CosmeticRegistry registry){
        this.cosmeticName = cosmeticName;
        this.setValue(cosmeticName);
        if (registry != null){
            registry.registerCosmetic(this);
        }
    }

    public @Nullable Material getDisplayMaterial() {
        return displayMaterial;
    }

    public Cosmetic setDisplayMaterial(Material displayMaterial) {
        this.displayMaterial = displayMaterial;
        return this;
    }

    public Component getCosmeticDisplayName(){
        return cosmeticDisplayName;
    }

    public Cosmetic setCosmeticDisplayName(Component cosmeticDisplayName) {
        this.cosmeticDisplayName = cosmeticDisplayName;
        return this;
    }

    public Cosmetic setCosmeticDisplayName(String cosmeticDisplayName) {
        return setCosmeticDisplayName(Component.text(cosmeticDisplayName, NamedTextColor.YELLOW));
    }

    public @NotNull String getCurrencyField(){
        return currencyField;
    }

    public int getPrice() {
        return price;
    }

    public Cosmetic setPrice(@NotNull String currencyField, int price) {
        this.currencyField = currencyField;
        this.price = price;
        return this;
    }

    public boolean hasPrice(){
        return currencyField != null;
    }

    public Cosmetic setPermission(@NotNull String permission){
        this.permission = permission;
        return this;
    }

    public @Nullable String getPermission() {
        return permission;
    }

    public boolean hasPermission(){
        return permission != null;
    }

    @ApiStatus.Internal
    public void addFieldMinimumCondition(FieldMinimumCondition fieldMinimumCondition){
        fieldMinimumConditions.add(fieldMinimumCondition);
    }

    public Cosmetic addFieldMinimumCondition(FieldMinimumCondition fieldMinimumCondition, Number minimumValue){
        fieldMinimumCondition.addCosmetic(this, minimumValue);
        return this;
    }

    public void addDocumentCountCondition(DocumentCountCondition documentCountCondition){
        documentCountConditions.add(documentCountCondition);
    }


    public boolean hasCosmeticConditions(){
        return !fieldMinimumConditions.isEmpty();
    }

    public boolean hasCosmeticCondition(FieldMinimumCondition condition){
        return fieldMinimumConditions.contains(condition);
    }
    public HashSet<FieldMinimumCondition> getFieldMinimumConditions() {
        return new HashSet<>(fieldMinimumConditions);
    }

    public HashSet<DocumentCountCondition> getDocumentCountConditions(){
        return new HashSet<>(documentCountConditions);
    }

    public Cosmetic setValue(int value){
        this.value = value;
        return this;
    }

    public Cosmetic setValue(String value){
        this.value = value;
        return this;
    }

    public Object getValue(){
        return this.value;
    }

    public String getCosmeticName() {
        return cosmeticName;
    }
}
