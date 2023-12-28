package MCCore.utils.InventoryUtils;


import MCCore.minigameAPI.MinigameHandler;
import MCCore.utils.Items;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopGUI extends ChestGUI{

    final String cosmeticUnlockListKey;
    final String cosmeticGroupName;

    final String cosmeticSelectedKey;

    public ShopGUI(int rows, String name, String cosmeticUnlockListKey, String cosmeticSelectedKey, String cosmeticGroupName) {
        super(rows, name);
        this.cosmeticUnlockListKey = cosmeticUnlockListKey;
        this.cosmeticSelectedKey = cosmeticSelectedKey;
        this.cosmeticGroupName = cosmeticGroupName;
    }

    public String getCosmeticUnlockListKey() {
        return cosmeticUnlockListKey;
    }

    public String getCosmeticGroupName() {
        return cosmeticGroupName;
    }

    public String getCosmeticSelectedKey() {
        return cosmeticSelectedKey;
    }

    public void setCosmeticResetSlot(int slot, Object resetValue, MinigameHandler handler){
        setCosmeticResetSlot(slot, resetValue, Material.STRUCTURE_VOID, handler);
    }

    public void setCosmeticResetSlot(int slot, Object resetValue, Material material, MinigameHandler handler){
        ItemStack resetItem = Items.makeItem(material, 1, ChatColor.RED+"Reset "+cosmeticGroupName);
        Items.setLore(resetItem, new String[]{ChatColor.YELLOW+"Click to reset your selected "+cosmeticGroupName});
        new GUIItem(this, slot, resetItem, click ->{
            ShopUtils.resetCosmetic((Player) click.getWhoClicked(), cosmeticSelectedKey, resetValue, cosmeticGroupName, handler);
        });
    }
}
