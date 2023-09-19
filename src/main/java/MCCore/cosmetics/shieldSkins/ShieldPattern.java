package MCCore.cosmetics.shieldSkins;

import MCCore.utils.Items;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class ShieldPattern {
    private ItemMeta meta = new ItemStack(Material.SHIELD).getItemMeta();

    public ShieldPattern addPattern(DyeColor color, PatternType patternType){
        BlockStateMeta bMeta = (BlockStateMeta) meta;
        Banner banner = (Banner) bMeta.getBlockState();
        banner.addPattern(new Pattern(color, patternType));

        banner.update();

        bMeta.setBlockState(banner);
        meta = bMeta;
        return this;
    }

    public ItemMeta getMeta(){
        return meta;
    }

    public ItemStack getItem(String itemName){
        ItemStack shield = Items.makeItem(Material.SHIELD, 1, itemName);
        ItemMeta newMeta = meta.clone();
        newMeta.setDisplayName(itemName);
        shield.setItemMeta(newMeta);
        shield.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        return shield;
    }
}
