package MCCore.cosmetics.shieldSkins;


import MCCore.cosmetics.Cosmetic;
import MCCore.cosmetics.shieldSkins.holidaySkins.Christmas;
import MCCore.cosmetics.shieldSkins.holidaySkins.Valentines;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;;import java.text.SimpleDateFormat;
import java.util.Date;


public abstract class ShieldSkin extends Cosmetic {

    private ShieldPattern shieldPattern;
    private static ShieldSkin valentines = new Valentines();
    private static ShieldSkin christmas = new Christmas();

    public ShieldSkin(String skinName, ShieldPattern shieldPattern){
        super(skinName);
        this.shieldPattern = shieldPattern;
    }

    public ShieldSkin(String skinName){
        super(skinName);
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
        shield.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        return shield;
    }

    public ItemStack applySkin(ItemStack shield){
        if (!shield.getType().equals(Material.SHIELD)){
            return null;
        }
    //Get Name and Damage
        Component displayName = Component.text(ChatColor.WHITE+"Shield");
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
        shield.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        return shield;
    }
    public ShieldPattern getShieldPattern() {
        return shieldPattern;
    }

    public static boolean attemptApplySpecial(ItemStack shield){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String dateString = formatter.format(date);
        if (dateString.contains("02/14")){
            valentines.applySkin(shield);
            return true;
        }
        else if (dateString.contains("12/25")){
            christmas.applySkin(shield);
            return true;
        }

        return false;
    }
}
