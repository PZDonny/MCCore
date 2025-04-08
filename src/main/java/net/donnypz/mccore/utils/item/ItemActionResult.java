package net.donnypz.mccore.utils.item;

import net.donnypz.mccore.minigame.arenaManager.Arena;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemActionResult {

    boolean cancelConsume = false;
    Player player;
    ItemStack itemStack;
    ItemStack replacementItem;
    Arena arena;
    ItemActionResult(Player player, ItemStack itemStack, @Nullable ItemStack replacementItem, Arena arena){
        this.player = player;
        this.itemStack = itemStack;
        this.replacementItem = replacementItem;
        this.arena = arena;
    }

    public void cancelConsume(){
        this.cancelConsume = true;
    }

    public Player player() {
        return player;
    }

    public ItemStack itemStack() {
        return itemStack;
    }

    public ItemStack replacementItem() {
        return replacementItem;
    }

    public Arena arena() {
        return arena;
    }
}
