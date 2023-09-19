package MCCore.utils.InventoryUtils;


import MCCore.utils.Items;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopGUI extends ChestGUI{

    final String cosmeticUnlockList;
    final String cosmeticGroupName;

    final String cosmeticSelectedKey;

    public ShopGUI(int rows, String name, String cosmeticUnlockList, String cosmeticSelectedKey, String cosmeticGroupName) {
        super(rows, name);
        this.cosmeticUnlockList = cosmeticUnlockList;
        this.cosmeticSelectedKey = cosmeticSelectedKey;
        this.cosmeticGroupName = cosmeticGroupName;
    }

    public String getCosmeticUnlockList() {
        return cosmeticUnlockList;
    }

    public String getCosmeticGroupName() {
        return cosmeticGroupName;
    }

    public String getCosmeticSelectedKey() {
        return cosmeticSelectedKey;
    }

    public void setCosmeticResetSlot(int slot, Object resetValue){
        ItemStack resetItem = Items.makeItem(Material.STRUCTURE_VOID, 1, ChatColor.RED+"Reset "+cosmeticGroupName);
        Items.setLore(resetItem, new String[]{ChatColor.YELLOW+"Click to reset your selected "+cosmeticGroupName});
        new GUIItem(this, slot, resetItem, click ->{
            ShopUtils.resetCosmetic((Player) click.getWhoClicked(), cosmeticSelectedKey, resetValue, cosmeticGroupName);
        });
    }
}
