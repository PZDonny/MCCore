package net.donnypz.mccore.cosmetics.shieldSkins;


import net.donnypz.mccore.cosmetics.Cosmetic;
import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.cosmetics.shieldSkins.holidaySkins.Christmas;
import net.donnypz.mccore.cosmetics.shieldSkins.holidaySkins.Valentines;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import java.text.SimpleDateFormat;
import java.util.Date;


public abstract class ShieldSkin extends Cosmetic {

    private ShieldPattern shieldPattern;

    public ShieldSkin(String skinName, ShieldPattern shieldPattern, CosmeticRegistry registry){
        super(skinName, registry);
        this.shieldPattern = shieldPattern;
    }

    public ShieldSkin(String skinName, CosmeticRegistry registry){
        super(skinName, registry);
    }

    public void setShieldPattern(ShieldPattern shieldPattern){
        this.shieldPattern = shieldPattern;
    }

    public static ItemStack applySkin(ItemStack shield, ShieldSkin skin){
        if (!shield.getType().equals(Material.SHIELD)){
            return null;
        }
        //Get Name and Damage
        Component displayName = Component.text("Shield");
        if (shield.getItemMeta().displayName() != null){
            displayName = shield.getItemMeta().displayName();
        }
        int damage = ((Damageable) shield.getItemMeta()).getDamage();

        //Apply Pattern
        shield.setItemMeta(skin.shieldPattern.getMeta());

        //Apply Damage and Name
        Damageable damageMeta = (Damageable) shield.getItemMeta();
        damageMeta.displayName(displayName);
        damageMeta.setDamage(damage);
        shield.setItemMeta(damageMeta);
        shield.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        return shield;
    }

    public ItemStack applySkin(ItemStack shield){
        if (!shield.getType().equals(Material.SHIELD)){
            return null;
        }
    //Get Name and Damage
        Component displayName = Component.text("Shield", NamedTextColor.WHITE);
        if (shield.getItemMeta().displayName() != null){
            displayName = shield.getItemMeta().displayName();
        }
        int damage = ((Damageable) shield.getItemMeta()).getDamage();

    //Apply Pattern
        shield.setItemMeta(shieldPattern.getMeta());

    //Apply Damage and Name
        Damageable damageMeta = (Damageable) shield.getItemMeta();
        damageMeta.displayName(displayName);
        damageMeta.setDamage(damage);
        shield.setItemMeta(damageMeta);
        shield.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        return shield;
    }
    public ShieldPattern getShieldPattern() {
        return shieldPattern;
    }
}
