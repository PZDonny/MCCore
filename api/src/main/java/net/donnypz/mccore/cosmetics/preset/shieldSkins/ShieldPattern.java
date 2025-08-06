package net.donnypz.mccore.cosmetics.preset.shieldSkins;

import net.donnypz.mccore.utils.item.ItemBuilder;
import net.donnypz.mccore.version.CoreAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
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
        ItemStack shield = new ItemBuilder(Material.SHIELD)
                .setDisplayName(Component.text(itemName))
                .setAmount(1)
                .build();
        ItemMeta newMeta = meta.clone();
        newMeta.setDisplayName(itemName);
        shield.setItemMeta(newMeta);
        CoreAPI.getVersionHandler().hideAdditionalTooltip(shield);
        //shield.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.BANNER_PATTERNS).build());
        return shield;
    }
}
