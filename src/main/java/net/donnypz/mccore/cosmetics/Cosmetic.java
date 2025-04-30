package net.donnypz.mccore.cosmetics;

import net.donnypz.mccore.database.cosmeticConditions.CosmeticCondition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public abstract class Cosmetic{
    private final String cosmeticName;
    private Object selectValue;
    private Component cosmeticDisplayName = Component.empty();
    private Currency currency;
    private String permission;
    private Material displayMaterial = Material.STICK;
    private final HashSet<CosmeticCondition> unlockConditions = new HashSet<>();

    public Cosmetic(@NotNull String cosmeticName, @Nullable CosmeticRegistry registry){
        this.cosmeticName = cosmeticName;
        this.setSelectValue(cosmeticName);
        if (registry != null){
            registry.registerCosmetic(this);
        }
    }

    public Cosmetic setDisplayMaterial(@NotNull Material displayMaterial) {
        this.displayMaterial = displayMaterial;
        return this;
    }

    public @NotNull Material getDisplayMaterial() {
        return displayMaterial;
    }

    public Cosmetic setCosmeticDisplayName(@NotNull Component cosmeticDisplayName) {
        this.cosmeticDisplayName = cosmeticDisplayName;
        return this;
    }

    public Cosmetic setCosmeticDisplayName(@NotNull String cosmeticDisplayName) {
        return setCosmeticDisplayName(Component.text(cosmeticDisplayName, NamedTextColor.YELLOW));
    }

    public @NotNull Component getCosmeticDisplayName(){
        return cosmeticDisplayName;
    }

    public Cosmetic setCurrency(@NotNull Currency currency){
        this.currency = currency;
        return this;
    }

    public Cosmetic setCurrency(int price, @NotNull String currencyField, @NotNull String displayName) {
        this.currency = new Currency(price, currencyField, displayName);
        return this;
    }

    public @Nullable Currency getCurrency(){
        return currency;
    }

    public boolean hasCurrency(){
        return currency != null;
    }

    public boolean hasPermission(){
        return permission != null;
    }


    public Cosmetic addCondition(@NotNull CosmeticCondition condition){
        unlockConditions.add(condition);
        return this;
    }

    public boolean hasConditions(){
        return !unlockConditions.isEmpty();
    }

    public boolean hasCondition(@NotNull CosmeticCondition condition){
        return unlockConditions.contains(condition);
    }

    public HashSet<CosmeticCondition> getConditions() {
        return new HashSet<>(unlockConditions);
    }

    public Cosmetic setSelectValue(int selectValue){
        this.selectValue = selectValue;
        return this;
    }

    public Cosmetic setSelectValue(@NotNull String value){
        this.selectValue = value;
        return this;
    }

    public Object getSelectValue(){
        return this.selectValue;
    }

    public String getCosmeticName() {
        return cosmeticName;
    }

    public record Currency(int price, @NotNull String currencyField, @NotNull String displayName){};
}
